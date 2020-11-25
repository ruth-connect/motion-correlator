package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
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

	private static final Logger logger = LoggerFactory.getLogger(MotionCorrelatorServiceImpl.class);

	@Override
	public void correlateMotionAndPersonDetections(String camera, String vectorData)
			throws IOException, URISyntaxException {
		VectorDataList vectorDataList = vectorDataService.parseVectorData(vectorData);
		logger.info("Got vector data for camera " + camera);
		Frame frame = frameService.getFrame(camera, vectorDataList.getTimestamp());
		logger.info("Got image from camera: " + camera);
		PersonDetections personDetections = personDetectionService.detectPersonsFromDelta(camera, frame);
		logger.info("Finished getting vector data and person detection data for camera: " + camera);

		MotionCorrelation motionCorrelation = new MotionCorrelation();
		motionCorrelation.setVectorData(vectorDataList);
		motionCorrelation.setImage(frame.getImage());
		motionCorrelation.setPersonDetections(personDetections);
		logger.info("Motion correlation data for camera " + camera + ": " + motionCorrelation);

		imageStampingService.stampImage(motionCorrelation);
		imageService.writeImage(camera, motionCorrelation.getImage(), motionCorrelation.getPersonDetections(), false);
		imageService.writeImage(camera, motionCorrelation.getStampedImage(), motionCorrelation.getPersonDetections(),
				true);
		imageService.writeImage(camera,
				new Image(frame.getTimestamp(), ImageUtils.encodeImage(frame.getAverageFrame())), "-average");
		imageService.writeImage(camera, personDetections.getDelta(), "-delta");
	}
}
