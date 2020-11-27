package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
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

	private MotionCorrelator motionCorrelator;

	private static final Logger logger = LoggerFactory.getLogger(MotionCorrelatorServiceImpl.class);

	@PostConstruct
	public void initialise() {
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
		private Thread motionCorrelatorThread;

		public void initialise() {
			motionCorrelatorThread = new Thread(this, "Motion Correlator");
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
					VectorDataList vectorDataList = vectorDataQueue.take();
					performMotionCorrelation(vectorDataList);
				} catch (Exception ex) {
					logger.error("Failed performing motion correlation", ex);
				}
			}
		}

		private void performMotionCorrelation(VectorDataList vectorDataList) throws IOException {
			Frame frame = frameService.getFrame(vectorDataList.getCamera(), vectorDataList.getTimestamp());
			logger.info("Got image from camera: " + vectorDataList.getCamera());
			PersonDetections personDetections = personDetectionService
					.detectPersonsFromDelta(vectorDataList.getCamera(), frame);
			logger.info(
					"Finished getting vector data and person detection data for camera: " + vectorDataList.getCamera());

			MotionCorrelation motionCorrelation = new MotionCorrelation();
			motionCorrelation.setVectorData(vectorDataList);
			motionCorrelation.setImage(frame.getImage());
			motionCorrelation.setPersonDetections(personDetections);
			logger.info("Motion correlation data for camera " + vectorDataList.getCamera() + ": " + motionCorrelation);

			imageStampingService.stampImage(motionCorrelation);
			imageService.writeImage(vectorDataList.getCamera(), motionCorrelation.getImage(),
					motionCorrelation.getPersonDetections(), false);
			imageService.writeImage(vectorDataList.getCamera(), motionCorrelation.getStampedImage(),
					motionCorrelation.getPersonDetections(), true);
			imageService.writeImage(vectorDataList.getCamera(),
					new Image(frame.getTimestamp(), ImageUtils.encodeImage(frame.getAverageFrame())), "-average");
			imageService.writeImage(vectorDataList.getCamera(), personDetections.getDelta(), "-delta");

			if (personDetections.getPersonDetections().size() > 0) {
				homeAssistantService.notifyPersonDetected(cameraService.getCamera(vectorDataList.getCamera()),
						personDetections);
			}
		}
	}
}
