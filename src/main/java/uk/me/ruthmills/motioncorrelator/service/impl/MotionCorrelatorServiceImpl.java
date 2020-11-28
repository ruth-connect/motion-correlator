package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
import uk.me.ruthmills.motioncorrelator.service.ImageService;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;
import uk.me.ruthmills.motioncorrelator.service.MotionCorrelatorService;
import uk.me.ruthmills.motioncorrelator.service.PersonDetectionService;
import uk.me.ruthmills.motioncorrelator.service.VectorDataService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;
import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

@Service
public class MotionCorrelatorServiceImpl implements MotionCorrelatorService {

	@Autowired
	private VectorDataService vectorDataService;

	@Autowired
	private FrameService frameService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private PersonDetectionService personDetectionService;

	@Autowired
	private ImageStampingService imageStampingService;

	@Autowired
	private CameraService cameraService;

	@Autowired
	private HomeAssistantService homeAssistantService;

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

		private BlockingQueue<VectorDataList> vectorDataQueue = new LinkedBlockingDeque<>();
		private Map<String, MotionCorrelation> previousMotionDetectionMap = new HashMap<>();
		private Map<String, Frame> currentFrameMap = new HashMap<>();
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
					VectorDataList vectorDataList = vectorDataQueue.poll();
					if (vectorDataList != null) {
						// We have a new vector detection.
						String camera = vectorDataList.getCamera();
						MotionCorrelation currentMotionDetection = new MotionCorrelation(camera,
								vectorDataList.getTimestamp(), vectorDataList.getFrameVector());
						performMotionCorrelation(currentMotionDetection);

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

						// Set the current frame in the map for this camera.
						currentFrameMap.put(camera, currentMotionDetection.getFrame());

						// are we dealing with an existing vector detection? If so, do the logic for it.
					} else {
						// Round-robin through the cameras.
						String camera = getNextCamera();
						if (camera != null) {
							MotionCorrelation currentMotionDetection = new MotionCorrelation(camera);
							performMotionCorrelation(currentMotionDetection);

							// Is there a person detection?
							if (currentMotionDetection.getPersonDetections().getPersonDetections().size() > 0) {
								// add motion correlations with no frame vector for the last 3 seconds.
								addEmptyMotionCorrelationsForLast3Seconds(currentMotionDetection);
							}
						}
					}
				} catch (Exception ex) {
					logger.error("Failed performing motion correlation", ex);
				}
			}
		}

		private void performMotionCorrelation(MotionCorrelation motionCorrelation) throws IOException {
			Frame frame = frameService.getFrame(motionCorrelation.getCamera(), motionCorrelation.getVectorTimestamp());
			if (frame.getMotionCorrelation() == null) {
				frame.setMotionCorrelation(motionCorrelation);
				logger.info("Got image from camera: " + motionCorrelation.getCamera());
				PersonDetections personDetections = personDetectionService
						.detectPersonsFromDelta(motionCorrelation.getCamera(), frame);
				logger.info("Finished getting vector data and person detection data for camera: "
						+ motionCorrelation.getCamera());

				motionCorrelation.setFrame(frame);
				motionCorrelation.setPersonDetections(personDetections);
				logger.info("Motion correlation data for camera " + motionCorrelation.getCamera() + ": "
						+ motionCorrelation);

				imageStampingService.stampImage(motionCorrelation);
				imageService.writeImage(motionCorrelation.getCamera(), motionCorrelation.getFrame().getImage(),
						motionCorrelation.getPersonDetections(), false);
				imageService.writeImage(motionCorrelation.getCamera(), motionCorrelation.getStampedImage(),
						motionCorrelation.getPersonDetections(), true);
				imageService.writeImage(motionCorrelation.getCamera(),
						new Image(frame.getTimestamp(), ImageUtils.encodeImage(frame.getAverageFrame())), "-average");
				imageService.writeImage(motionCorrelation.getCamera(), personDetections.getDelta(), "-delta");

				if (personDetections.getPersonDetections().size() > 0) {
					homeAssistantService.notifyPersonDetected(cameraService.getCamera(motionCorrelation.getCamera()),
							personDetections);
				}
			}
		}

		private void interpolateVectorsOverTime(MotionCorrelation currentMotionDetection,
				MotionCorrelation previousMotionDetection) {
			// if both detections have frame vectors, and previous motion detection was
			// within 3 seconds, interpolate the vectors over time.
			long vectorTimeDifferenceMilliseconds = TimeUtils
					.toMilliseconds(currentMotionDetection.getVectorTimestamp())
					- TimeUtils.toMilliseconds(previousMotionDetection.getVectorTimestamp());
			if ((currentMotionDetection.getFrameVector() != null) && (previousMotionDetection.getFrameVector() != null)
					&& (vectorTimeDifferenceMilliseconds > 0) && (vectorTimeDifferenceMilliseconds <= 3000)) {
				interpolateVectorsOverTime(currentMotionDetection, previousMotionDetection,
						vectorTimeDifferenceMilliseconds);
			}
		}

		private void interpolateVectorsOverTime(MotionCorrelation currentMotionDetection,
				MotionCorrelation previousMotionDetection, long vectorTimeDifferenceMilliseconds) {
			Vector startVector = previousMotionDetection.getFrameVector();
			Vector endVector = currentMotionDetection.getFrameVector();

			long vectorStartTimeMilliseconds = TimeUtils.toMilliseconds(previousMotionDetection.getVectorTimestamp());
			long imageStartTimeMilliseconds = TimeUtils
					.toMilliseconds(previousMotionDetection.getFrame().getTimestamp());

			// extra time if image timestamp is before vector timestamp, less time
			// otherwise.
			long startOffsetMilliseconds = vectorStartTimeMilliseconds - imageStartTimeMilliseconds;

			long imageEndTimeMilliseconds = TimeUtils.toMilliseconds(currentMotionDetection.getFrame().getTimestamp());
			long vectorEndTimeMilliseconds = TimeUtils.toMilliseconds(currentMotionDetection.getVectorTimestamp());

			// extra time if image timestamp is after vector timestamp, less time otherwise.
			long endOffsetMilliseconds = imageEndTimeMilliseconds - vectorEndTimeMilliseconds;

			long imageTimeDifferenceMilliseconds = vectorTimeDifferenceMilliseconds + startOffsetMilliseconds
					+ endOffsetMilliseconds;

			Frame frame = previousMotionDetection.getFrame().getNextFrame();
			while (frame.getMotionCorrelation() == null) {
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
				frameVector.setMagnitude(
						interpolateIntValue(startVector.getMagnitude(), endVector.getMagnitude(), ratioTimeElapsed));
				frameVector
						.setCount(interpolateIntValue(startVector.getCount(), endVector.getCount(), ratioTimeElapsed));

				// Create the motion correlation for this frame.
				MotionCorrelation motionCorrelation = new MotionCorrelation(currentMotionDetection.getCamera(),
						frameVectorTime, frameVector);
				frame.setMotionCorrelation(motionCorrelation);
				logger.info("Interpolated data for image with timestamp: " + frame.getTimestamp() + " and camera: "
						+ currentMotionDetection.getCamera() + "\n" + motionCorrelation);

				// Get the next frame.
				frame = frame.getNextFrame();
			}
		}

		private int interpolateIntValue(int start, int end, double ratio) {
			double startProportion = ((double) start * (1d - ratio));
			double endProportion = ((double) end * ratio);
			return (int) Math.round(startProportion + endProportion);
		}

		private void addEmptyMotionCorrelationsForLast3Seconds(MotionCorrelation currentMotionDetection) {
			long currentImageTimeMilliseconds = TimeUtils
					.toMilliseconds(currentMotionDetection.getFrame().getTimestamp());
			Frame previousFrame = currentMotionDetection.getFrame().getPreviousFrame();
			if (previousFrame != null) {
				long previousImageTimeMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
				long imageTimeDifferenceMilliseconds = currentImageTimeMilliseconds - previousImageTimeMilliseconds;
				while (previousFrame != null && imageTimeDifferenceMilliseconds <= 3000) {
					previousFrame.setMotionCorrelation(new MotionCorrelation(currentMotionDetection.getCamera()));

					currentImageTimeMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
					previousFrame = previousFrame.getPreviousFrame();
					if (previousFrame != null) {
						previousImageTimeMilliseconds = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
						imageTimeDifferenceMilliseconds = currentImageTimeMilliseconds - previousImageTimeMilliseconds;
					}
				}
			}
		}

		private String getNextCamera() {
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
	}
}
