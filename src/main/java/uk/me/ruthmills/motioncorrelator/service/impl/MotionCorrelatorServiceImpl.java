package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;
import uk.me.ruthmills.motioncorrelator.service.ImageService;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;
import uk.me.ruthmills.motioncorrelator.service.MotionCorrelatorService;
import uk.me.ruthmills.motioncorrelator.service.PersonDetectionService;
import uk.me.ruthmills.motioncorrelator.service.VectorDataService;

@Service
public class MotionCorrelatorServiceImpl implements MotionCorrelatorService {

	@Autowired
	private VectorDataService vectorDataService;

	@Autowired
	private MjpegStreamService mjpegStreamService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private PersonDetectionService personDetectionService;

	@Autowired
	private ImageStampingService imageStampingService;

	private final Logger logger = LoggerFactory.getLogger(MotionCorrelatorServiceImpl.class);

	@Override
	public void correlateMotionAndPersonDetections(String camera, String vectorData)
			throws IOException, URISyntaxException {
		VectorDataList vectorDataList = vectorDataService.parseVectorData(vectorData);
		logger.info("Got vector data for camera " + camera);
		Image image = mjpegStreamService.getLatestImage(camera);
		logger.info("Got image from camera: " + camera);
		PersonDetections personDetection = personDetectionService.detectPersons(image);
		logger.info("Finished getting vector data and person detection data for camera: " + camera);

		MotionCorrelation motionCorrelation = new MotionCorrelation();
		motionCorrelation.setVectorData(vectorDataList);
		motionCorrelation.setImage(image);
		motionCorrelation.setPersonDetections(personDetection);
		logger.info("Motion correlation data for camera " + camera + ": " + motionCorrelation);

		imageStampingService.stampImage(motionCorrelation);
		imageService.writeImage(camera, motionCorrelation.getImage(), false);
		imageService.writeImage(camera, motionCorrelation.getStampedImage(), true);
	}
}
