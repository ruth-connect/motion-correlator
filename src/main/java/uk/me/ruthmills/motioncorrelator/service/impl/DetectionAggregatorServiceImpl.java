package uk.me.ruthmills.motioncorrelator.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.Detections;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.PersonProbability;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.DetectionAggregatorService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
import uk.me.ruthmills.motioncorrelator.service.ImageFileWritingService;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;

@Service
public class DetectionAggregatorServiceImpl implements DetectionAggregatorService {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private ImageStampingService imageStampingService;

	@Autowired
	private ImageFileWritingService imageFileWritingService;

	@Autowired
	private HomeAssistantService homeAssistantService;

	private DetectionAggregator detectionAggregator;

	private boolean diskOK = false;

	private static final Logger logger = LoggerFactory.getLogger(DetectionAggregatorServiceImpl.class);

	@PostConstruct
	public void initialise() {
		detectionAggregator = new DetectionAggregator();
		detectionAggregator.initialise();
	}

	@Override
	public void addDetection(MotionCorrelation motionCorrelation) {
		detectionAggregator.addDetection(motionCorrelation);
	}

	private class DetectionAggregator implements Runnable {

		private BlockingDeque<MotionCorrelation> detectionQueue = new LinkedBlockingDeque<>();
		private Map<String, Detections> detectionsForCameraMap = new HashMap<>();
		private Thread detectionAggregatorThread;

		public void initialise() {
			detectionAggregatorThread = new Thread(this, "Detection Aggregator");
			detectionAggregatorThread.setPriority(4); // lower priority than normal (5).
			detectionAggregatorThread.start();
			logger.info("Detection Aggregator Thread started");
		}

		public void addDetection(MotionCorrelation motionCorrelation) {
			detectionQueue.offerLast(motionCorrelation);
		}

		@Override
		public void run() {
			while (true) {
				try {
					MotionCorrelation motionCorrelation = detectionQueue.pollFirst(10, TimeUnit.MILLISECONDS);
					if (motionCorrelation != null) {
						logger.info("DETECTION AGGREGATOR. Motion correlation data for camera "
								+ motionCorrelation.getCamera() + ": " + motionCorrelation);

						// Write the images.
						writeImages(motionCorrelation);

						// Create the detection object.
						Detection detection = new Detection(motionCorrelation.getCamera(),
								motionCorrelation.getFrame().getSequence(), motionCorrelation.getFrame().getTimestamp(),
								motionCorrelation.getVectorMotionDetection(), motionCorrelation.getPersonDetections());

						// Add the detection to the list.
						Detections detections = getDetectionsForCamera(detection.getCamera());
						detections.addDetection(detection);

						// Get the person probability.
						PersonProbability personProbability = detections.getPersonProbability();
						logger.info(personProbability.toString());

						if (motionCorrelation.getPersonDetections().getPersonDetections().size() > 0) {
							homeAssistantService.notifyPersonDetected(cameraService.getCamera(detection.getCamera()),
									detection.getSequence(), detection.getTimestamp(), detection.getPersonDetections());
						}
					}

					// Remove expired detections.
					removeExpiredDetections();

				} catch (Exception ex) {
					logger.error("Failed performing detection aggregation", ex);
				}
			}
		}

		private void writeImages(MotionCorrelation motionCorrelation) {
			try {
				imageFileWritingService.writeImage(motionCorrelation.getCamera(),
						motionCorrelation.getFrame().getImage());
				imageFileWritingService.writeImage(motionCorrelation.getCamera(),
						imageStampingService.stampImage(motionCorrelation), motionCorrelation.getPersonDetections());
				imageFileWritingService.writeImage(motionCorrelation.getCamera(), motionCorrelation.getAverageFrame(),
						"-average");
				imageFileWritingService.writeImage(motionCorrelation.getCamera(), motionCorrelation.getDelta(),
						"-delta");

				if (!diskOK) {
					homeAssistantService.notifyDiskOK();
					diskOK = true;
				}
			} catch (Exception ex) {
				logger.info("Failed to write images", ex);
				if (diskOK) {
					homeAssistantService.notifyDiskFailed();
					diskOK = false;
				}
			}
		}

		private Detections getDetectionsForCamera(String camera) {
			Detections detections = detectionsForCameraMap.get(camera);
			if (detections == null) {
				detections = new Detections();
				detectionsForCameraMap.put(camera, detections);
			}
			return detections;
		}

		private void removeExpiredDetections() {
			detectionsForCameraMap.entrySet().stream().map(entry -> entry.getValue())
					.forEach(detections -> detections.removeExpiredDetections());
		}
	}
}
