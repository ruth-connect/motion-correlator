package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.DetectionAggregatorService;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.service.MotionCorrelatorService;
import uk.me.ruthmills.motioncorrelator.service.PersonDetectionService;
import uk.me.ruthmills.motioncorrelator.service.VectorDataService;
import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

@Service
public class MotionCorrelatorServiceImpl implements MotionCorrelatorService {

	@Autowired
	private VectorDataService vectorDataService;

	@Autowired
	private FrameService frameService;

	@Autowired
	private PersonDetectionService personDetectionService;

	@Autowired
	private DetectionAggregatorService detectionAggregatorService;

	@Autowired
	private CameraService cameraService;

	private List<Camera> cameras;
	private MotionCorrelator motionCorrelator;

	private static final Logger logger = LoggerFactory.getLogger(MotionCorrelatorServiceImpl.class);

	@PostConstruct
	public void initialise() {
		cameras = cameraService.getCameras();
		motionCorrelator = new MotionCorrelator();
		motionCorrelator.initialise();
	}

	@Override
	public void correlateMotionAndPersonDetections(String camera, String vectorData)
			throws IOException, URISyntaxException {
		VectorDataList vectorDataList = vectorDataService.parseVectorData(camera, vectorData);
		logger.info("Got vector data for camera " + camera);
		motionCorrelator.addVectorData(vectorDataList);
	}

	private class MotionCorrelator implements Runnable {

		private BlockingDeque<VectorDataList> vectorDataQueue = new LinkedBlockingDeque<>();
		private Map<String, MotionCorrelation> previousMotionDetectionMap = new HashMap<>();
		private int cameraIndex = 0;
		private Thread motionCorrelatorThread;

		public void initialise() {
			motionCorrelatorThread = new Thread(this, "Motion Correlator");
			motionCorrelatorThread.setPriority(4); // lower priority than normal (5).
			motionCorrelatorThread.start();
			logger.info("Motion Correlator Thread started");
		}

		public void addVectorData(VectorDataList vectorData) {
			vectorDataQueue.offer(vectorData);
		}

		@Override
		public void run() {
			while (true) {
				try {
					VectorDataList vectorDataList = vectorDataQueue.pollFirst();
					if (vectorDataList != null) {
						// We have a new vector detection.
						logger.info("NEW VECTOR DETECTION for camera: " + vectorDataList.getCamera()
								+ " with VECTOR timestamp: " + vectorDataList.getTimestamp());
						String camera = vectorDataList.getCamera();
						MotionCorrelation currentMotionDetection = new MotionCorrelation(camera,
								new VectorMotionDetection(vectorDataList.getTimestamp(),
										vectorDataList.getFrameVector()));
						performMotionCorrelation(currentMotionDetection, false);

						// Is there a previous motion detection for this camera?
						MotionCorrelation previousMotionDetection = previousMotionDetectionMap.get(camera);
						if (previousMotionDetection != null) {
							// if both detections have frame vectors, and previous motion detection was
							// within 3 seconds, interpolate the vectors over time.
							interpolateVectorsOverTime(currentMotionDetection, previousMotionDetection);
						} else {
							// add motion correlations with no frame vector for the last 3 seconds.
							addEmptyMotionCorrelationsForLast3Seconds(currentMotionDetection);
						}

						// Set the current motion detection as the previous motion detection for next
						// time round.
						previousMotionDetectionMap.put(camera, currentMotionDetection);

					} else {
						// Do we have an existing motion detection we want to person detect previous
						// frames on?
						MotionCorrelation currentMotionDetection = getLatestMotionDetection();
						if (currentMotionDetection != null) {
							// Run person detection on the motion detection.
							performMotionCorrelation(currentMotionDetection, false);
						} else {
							performMotionCorrelationOnNextCameraRoundRobin();
						}
					}
				} catch (Exception ex) {
					logger.error("Failed performing motion correlation", ex);
				}
			}
		}

		private void performMotionCorrelation(MotionCorrelation motionCorrelation, boolean speculativeRoundRobin)
				throws IOException {
			Frame frame = motionCorrelation.getFrame();
			if (frame == null && motionCorrelation.getVectorMotionDetection() != null) {
				frame = frameService.getFrame(motionCorrelation.getCamera(),
						motionCorrelation.getVectorMotionDetection().getTimestamp());
			}
			if (frame != null) {
				if (frame.getMotionCorrelation() == null) {
					frame.setMotionCorrelation(motionCorrelation);
				}

				if (frame.getAverageFrame() == null) {
					logger.warn("No average frame for frame: " + frame.getTimestamp() + " for camera: "
							+ motionCorrelation.getCamera());
				} else {
					personDetectionService.detectPersonsFromDelta(motionCorrelation);
					motionCorrelation.setFrame(frame);

					// Send to the detection aggregator service if this is not a speculative round
					// robin - i.e. we have an actual or recent detection.
					if (!speculativeRoundRobin) {
						detectionAggregatorService.addDetection(motionCorrelation);
					}
				}
			}
		}

		private void interpolateVectorsOverTime(MotionCorrelation currentMotionDetection,
				MotionCorrelation previousMotionDetection) {
			if (currentMotionDetection.getVectorMotionDetection() != null
					&& previousMotionDetection.getVectorMotionDetection() != null) {
				// if both detections have frame vectors, and previous motion detection was
				// within 3 seconds, interpolate the vectors over time.
				long vectorTimeDifferenceMilliseconds = TimeUtils
						.toMilliseconds(currentMotionDetection.getVectorMotionDetection().getTimestamp())
						- TimeUtils.toMilliseconds(previousMotionDetection.getVectorMotionDetection().getTimestamp());
				if ((currentMotionDetection.getVectorMotionDetection().getFrameVector() != null)
						&& (previousMotionDetection.getVectorMotionDetection().getFrameVector() != null)
						&& (vectorTimeDifferenceMilliseconds > 0) && (vectorTimeDifferenceMilliseconds <= 3000)) {
					interpolateVectorsOverTime(currentMotionDetection, previousMotionDetection,
							vectorTimeDifferenceMilliseconds);
				} else {
					// add motion correlations with no frame vector for the last 3 seconds.
					addEmptyMotionCorrelationsForLast3Seconds(currentMotionDetection);
				}
			} else {
				// add motion correlations with no frame vector for the last 3 seconds.
				addEmptyMotionCorrelationsForLast3Seconds(currentMotionDetection);
			}
		}

		private void interpolateVectorsOverTime(MotionCorrelation currentMotionDetection,
				MotionCorrelation previousMotionDetection, long vectorTimeDifferenceMilliseconds) {
			Vector startVector = previousMotionDetection.getVectorMotionDetection().getFrameVector();
			Vector endVector = currentMotionDetection.getVectorMotionDetection().getFrameVector();

			long vectorStartTimeMilliseconds = TimeUtils
					.toMilliseconds(previousMotionDetection.getVectorMotionDetection().getTimestamp());
			if (previousMotionDetection.getFrame() != null) {
				long imageStartTimeMilliseconds = TimeUtils
						.toMilliseconds(previousMotionDetection.getFrame().getTimestamp());

				// extra time if image timestamp is before vector timestamp, less time
				// otherwise.
				long startOffsetMilliseconds = vectorStartTimeMilliseconds - imageStartTimeMilliseconds;

				long imageEndTimeMilliseconds = TimeUtils
						.toMilliseconds(currentMotionDetection.getFrame().getTimestamp());
				long vectorEndTimeMilliseconds = TimeUtils
						.toMilliseconds(currentMotionDetection.getVectorMotionDetection().getTimestamp());

				// extra time if image timestamp is after vector timestamp, less time otherwise.
				long endOffsetMilliseconds = imageEndTimeMilliseconds - vectorEndTimeMilliseconds;

				long imageTimeDifferenceMilliseconds = vectorTimeDifferenceMilliseconds + startOffsetMilliseconds
						+ endOffsetMilliseconds;

				Frame frame = previousMotionDetection.getFrame().getNextFrame();

				// Is there no motion correlation OR is there a motion correlation with no
				// vector?
				while (frame.getMotionCorrelation() == null || (frame.getMotionCorrelation() != null
						&& frame.getMotionCorrelation().getVectorMotionDetection() == null)) {
					// Calculate the ratio of the current frame image time between the start and end
					// image times.
					long frameImageTimeMilliseconds = TimeUtils.toMilliseconds(frame.getTimestamp());
					long frameTimeDifferenceMilliseconds = frameImageTimeMilliseconds - imageStartTimeMilliseconds;
					double ratioTimeElapsed = (double) frameTimeDifferenceMilliseconds
							/ (double) imageTimeDifferenceMilliseconds;

					// Interpolate the frame vector time.
					long frameVectorTimeMilliseconds = vectorStartTimeMilliseconds
							+ Math.round((double) vectorTimeDifferenceMilliseconds * ratioTimeElapsed);
					LocalDateTime frameVectorTime = TimeUtils.fromMilliseconds(frameVectorTimeMilliseconds);

					// Interpolate the vectors.
					Vector frameVector = new Vector();
					frameVector.setX(interpolateIntValue(startVector.getX(), endVector.getX(), ratioTimeElapsed));
					frameVector.setY(interpolateIntValue(startVector.getY(), endVector.getY(), ratioTimeElapsed));
					frameVector.setDx(interpolateIntValue(startVector.getDx(), endVector.getDx(), ratioTimeElapsed));
					frameVector.setDy(interpolateIntValue(startVector.getDy(), endVector.getDy(), ratioTimeElapsed));
					frameVector.setMagnitude(interpolateIntValue(startVector.getMagnitude(), endVector.getMagnitude(),
							ratioTimeElapsed));
					frameVector.setCount(
							interpolateIntValue(startVector.getCount(), endVector.getCount(), ratioTimeElapsed));

					VectorMotionDetection vectorMotionDetection = new VectorMotionDetection(frameVectorTime,
							frameVector, true);

					// Add the vector to the motion correlation for this frame.
					if (frame.getMotionCorrelation() == null) {
						frame.setMotionCorrelation(
								new MotionCorrelation(currentMotionDetection.getCamera(), vectorMotionDetection));
					} else {
						frame.getMotionCorrelation().setVectorMotionDetection(vectorMotionDetection);

						// Replace previous detection (without vector) with updated detection (with
						// interpolated vector).
						detectionAggregatorService.addDetection(frame.getMotionCorrelation());
					}

					logger.info("Interpolated data for image with timestamp: " + frame.getTimestamp() + " and camera: "
							+ currentMotionDetection.getCamera() + "\n" + frame.getMotionCorrelation());

					// Get the next frame.
					frame = frame.getNextFrame();
				}
			}
		}

		private int interpolateIntValue(int start, int end, double ratio) {
			double startProportion = ((double) start * (1d - ratio));
			double endProportion = ((double) end * ratio);
			return (int) Math.round(startProportion + endProportion);
		}

		private void addEmptyMotionCorrelationsForLast3Seconds(MotionCorrelation currentMotionDetection) {
			logger.info("addEmptyMotionCorrelationsForLast3Seconds called for camera: "
					+ currentMotionDetection.getCamera());
			if (currentMotionDetection.getFrame() != null) {
				logger.info("Current motion detection has a frame");
				long currentImageTimeMilliseconds = TimeUtils
						.toMilliseconds(currentMotionDetection.getFrame().getTimestamp());
				Frame previousFrame = currentMotionDetection.getFrame().getPreviousFrame();
				if (previousFrame != null) {
					logger.info("Executing...");
					long previousImageTimeMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
					long imageTimeDifferenceMilliseconds = currentImageTimeMilliseconds - previousImageTimeMilliseconds;
					while (previousFrame != null && imageTimeDifferenceMilliseconds <= 3000) {
						if (previousFrame.getMotionCorrelation() == null) {
							logger.info("Adding empty motion correlation for frame with timestamp: "
									+ previousFrame.getTimestamp() + " and camera: "
									+ currentMotionDetection.getCamera());
							previousFrame.setMotionCorrelation(
									new MotionCorrelation(currentMotionDetection.getCamera(), previousFrame));
						} else {
							logger.info("Skipping frame: " + previousFrame.getTimestamp() + " for camera: "
									+ currentMotionDetection.getCamera() + " as it already has a motion correlation.");
						}

						previousFrame = previousFrame.getPreviousFrame();
						if (previousFrame != null) {
							previousImageTimeMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
							imageTimeDifferenceMilliseconds = currentImageTimeMilliseconds
									- previousImageTimeMilliseconds;
						}
					}
				} else {
					logger.info("Not executing. Previous frame is null");
				}
			}
		}

		private MotionCorrelation getLatestMotionDetection() {
			List<MotionCorrelation> latestMotionDetections = previousMotionDetectionMap.entrySet().stream()
					.map(entry -> entry.getValue())
					.filter(entry -> entry != null && entry.getFrame() != null && entry.getFrameTimestamp() != null)
					.collect(Collectors.toList());
			latestMotionDetections.sort(Comparator.comparing(MotionCorrelation::getFrameTimestamp).reversed());
			for (MotionCorrelation motionDetection : latestMotionDetections) {
				Frame frame = motionDetection.getFrame();
				while (frame != null && frame.getMotionCorrelation() != null) {
					if (frame.getMotionCorrelation().getPersonDetections() == null) {
						return frame.getMotionCorrelation();
					} else {
						frame = frame.getPreviousFrame();
					}
				}
			}
			return null;
		}

		private void performMotionCorrelationOnNextCameraRoundRobin() throws IOException {
			MotionCorrelation currentMotionDetection;
			// Round-robin through the cameras.
			String camera = getNextCamera();
			if (camera != null) {
				// Get the latest frame for the camera.
				Frame frame = frameService.getLatestFrame(camera);

				// Is there a frame, and no motion correlation for this frame?
				if (frame != null && frame.getMotionCorrelation() == null) {
					currentMotionDetection = new MotionCorrelation(camera, frame);
					performMotionCorrelation(currentMotionDetection, true);

					// Is there a person detection?
					if (currentMotionDetection.getPersonDetections() != null
							&& currentMotionDetection.getPersonDetections().getPersonDetections().size() > 0) {
						logger.info("ROUND ROBIN person detection at timestamp: "
								+ currentMotionDetection.getFrameTimestamp() + " for camera: " + camera);

						// add motion correlations with no frame vector for the last 3 seconds.
						addEmptyMotionCorrelationsForLast3Seconds(currentMotionDetection);

						// Set the current motion detection as the previous motion detection for next
						// time round.
						previousMotionDetectionMap.put(camera, currentMotionDetection);
					} else if (isDetectionInLast3Seconds(currentMotionDetection)) {
						logger.info("ROUND ROBIN - detection within last 3 seconds - adding empty motion correlations");

						// add motion correlations with no frame vector for the last 3 seconds.
						addEmptyMotionCorrelationsForLast3Seconds(currentMotionDetection);

						// Set the current motion detection as the previous motion detection for next
						// time round.
						previousMotionDetectionMap.put(camera, currentMotionDetection);
					} else {
						// No person detections, so clear the motion detection.
						frame.setMotionCorrelation(null);
					}
				}
			}
		}

		String getNextCamera() {
			int currentIndex = cameraIndex;
			getNextCameraIndex();
			Camera camera = cameras.get(cameraIndex);
			if (camera.isConnected()) {
				return camera.getName();
			}
			while (cameraIndex != currentIndex) {
				getNextCameraIndex();
				if (camera.isConnected()) {
					return camera.getName();
				}
			}
			return null;
		}

		private void getNextCameraIndex() {
			cameraIndex++;
			if (cameraIndex >= cameras.size()) {
				cameraIndex = 0;
			}
		}

		boolean isDetectionInLast3Seconds(MotionCorrelation currentMotionCorrelation) {
			long currentImageTimestampMilliseconds = TimeUtils
					.toMilliseconds(currentMotionCorrelation.getFrameTimestamp());
			Frame previousFrame = currentMotionCorrelation.getFrame().getPreviousFrame();
			if (previousFrame != null) {
				long previousImageTimestampMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
				while (previousFrame != null
						&& (currentImageTimestampMilliseconds - previousImageTimestampMilliseconds <= 3000)) {

					// Is there a previous detection?
					if (previousFrame.getMotionCorrelation() != null) {
						MotionCorrelation previousMotionCorrelation = previousFrame.getMotionCorrelation();
						if (previousMotionCorrelation.getVectorMotionDetection() != null
								|| (previousMotionCorrelation.getPersonDetections() != null && previousMotionCorrelation
										.getPersonDetections().getPersonDetections().size() > 0)) {
							return true;
						}
					}

					// Not a previous detection this time - get previous.
					previousFrame = previousFrame.getPreviousFrame();
					if (previousFrame != null) {
						previousImageTimestampMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
					}
				}
			}
			return false;
		}
	}
}
