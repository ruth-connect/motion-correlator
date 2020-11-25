package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.StopWatch;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.PersonDetectionService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class PersonDetectionServiceImpl implements PersonDetectionService {

	private HOGDescriptor hogDescriptor;

	private static final Logger logger = LoggerFactory.getLogger(PersonDetectionServiceImpl.class);

	@PostConstruct
	public void initialise() throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		hogDescriptor = new HOGDescriptor();
		hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
	}

	@Override
	public PersonDetections detectPersons(Image image) {
		return detectPersons(image, new PersonDetectionParameters());
	}

	@Override
	public PersonDetections detectPersons(Image image, PersonDetectionParameters personDetectionParameters) {
		Mat frame = ImageUtils.decodeImage(image, personDetectionParameters.getImageWidthPixels());
		logger.info("Frame size: " + frame.size());
		PersonDetections personDetections = detect(frame, personDetectionParameters);
		personDetections.setTimestamp(image.getTimestamp());
		frame.release();
		return personDetections;
	}

	@Override
	public PersonDetections detectPersonsFromDelta(String camera, Frame frame) {
		if (frame.getPreviousFrame() == null) {
			return detectPersons(frame.getImage()); // fall back to just detecting from image.
		}

		Mat averageFrame = frame.getPreviousFrame().getAverageFrame();
		PersonDetectionParameters personDetectionParameters = new PersonDetectionParameters();
		Mat blurredFrame = frame.getBlurredFrame();

		Mat absAverageFrame = new Mat();
		Core.convertScaleAbs(averageFrame, absAverageFrame);
		Mat frameDelta = new Mat();
		Core.absdiff(blurredFrame, absAverageFrame, frameDelta);
		absAverageFrame.release();

		PersonDetections personDetections = detect(frameDelta, personDetectionParameters);
		personDetections.setTimestamp(frame.getTimestamp());

		Image delta = new Image(frame.getTimestamp(), ImageUtils.encodeImage(frameDelta));
		frameDelta.release();

		personDetections.setDelta(delta);

		return personDetections;
	}

	private PersonDetections detect(Mat frame, PersonDetectionParameters personDetectionParameters) {
		MatOfRect foundLocations = new MatOfRect();
		MatOfDouble foundWeights = new MatOfDouble();
		double hitThreshold = personDetectionParameters.getHitThreshold();
		Size winStride = new Size(personDetectionParameters.getWinStrideX(), personDetectionParameters.getWinStrideY());
		Size padding = new Size(personDetectionParameters.getPaddingX(), personDetectionParameters.getPaddingY());
		double scale = personDetectionParameters.getScale();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		hogDescriptor.detectMultiScale(frame, foundLocations, foundWeights, hitThreshold, winStride, padding, scale);
		stopWatch.stop();
		List<Rect> locations = foundLocations.toList();
		List<PersonDetection> personDetections = new ArrayList<>();
		for (int detectionIndex = 0; detectionIndex < locations.size(); detectionIndex++) {
			PersonDetection personDetection = new PersonDetection(personDetectionParameters.getImageWidthPixels());
			personDetection.setLocation(locations.get(detectionIndex));
			List<Double> weights = new ArrayList<>();
			for (int weightIndex = 0; weightIndex < foundWeights.cols(); weightIndex++) {
				weights.add(foundWeights.get(detectionIndex, weightIndex)[0]);
			}
			personDetection.setWeights(weights);
			personDetections.add(personDetection);
		}
		personDetections.sort(Comparator.comparing(PersonDetection::getWeight).reversed());
		logger.info("Number of person detections: " + personDetections.size());
		foundLocations.release();
		foundWeights.release();
		return new PersonDetections(personDetections, stopWatch.getTime(TimeUnit.MILLISECONDS));
	}
}
