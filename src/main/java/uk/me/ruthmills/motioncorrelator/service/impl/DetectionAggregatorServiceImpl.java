package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.DetectionAggregatorService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
import uk.me.ruthmills.motioncorrelator.service.ImageService;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class DetectionAggregatorServiceImpl implements DetectionAggregatorService {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private ImageStampingService imageStampingService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private HomeAssistantService homeAssistantService;

	private DetectionAggregator detectionAggregator;

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
		private Map<String, List<Detection>> detectionsForCameraMap = new HashMap<>();
		private Thread detectionAggregatorThread;

		public void initialise() {
			detectionAggregatorThread = new Thread(this, "Detection Aggregator");
			detectionAggregatorThread.setPriority(4); // lower priority than normal (5).
			detectionAggregatorThread.start();
			logger.info("Detection Aggregator Thread started");
		}

		public void addDetection(MotionCorrelation motionCorrelation) {
			detectionQueue.offer(motionCorrelation);
		}

		@Override
		public void run() {
			while (true) {
				try {
					MotionCorrelation motionCorrelation = detectionQueue.takeFirst();

					logger.info("DETECTION AGGREGATOR. Motion correlation data for camera "
							+ motionCorrelation.getCamera() + ": " + motionCorrelation);

					// Write the images.
					writeImages(motionCorrelation);

					// Create the detection object.
					Detection detection = new Detection(motionCorrelation.getCamera(),
							motionCorrelation.getVectorMotionDetection(), motionCorrelation.getPersonDetections());

					// Add the detection to the list.
//					getDetectionListForCamera(detection.getCamera()).add(detection);

					if (motionCorrelation.getPersonDetections().getPersonDetections().size() > 0) {
						homeAssistantService.notifyPersonDetected(
								cameraService.getCamera(motionCorrelation.getCamera()),
								motionCorrelation.getPersonDetections());
					}
				} catch (Exception ex) {
					logger.error("Failed performing detection aggregation", ex);
				}
			}
		}

		private void writeImages(MotionCorrelation motionCorrelation) throws IOException {
			imageService.writeImage(motionCorrelation.getCamera(), motionCorrelation.getFrame().getImage());
			imageService.writeImage(motionCorrelation.getCamera(), imageStampingService.stampImage(motionCorrelation),
					motionCorrelation.getPersonDetections());
			imageService
					.writeImage(motionCorrelation.getCamera(),
							new Image(motionCorrelation.getFrame().getTimestamp(),
									ImageUtils.encodeImage(motionCorrelation.getFrame().getAverageFrame())),
							"-average");
			imageService.writeImage(motionCorrelation.getCamera(), motionCorrelation.getDelta(), "-delta");
		}

		private List<Detection> getDetectionListForCamera(String camera) {
			List<Detection> detectionList = detectionsForCameraMap.get(camera);
			if (detectionList == null) {
				detectionList = new ArrayList<>();
				detectionsForCameraMap.put(camera, detectionList);
			}
			return detectionList;
		}
	}
}
