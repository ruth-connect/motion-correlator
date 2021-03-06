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

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
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
		PersonDetections personDetections = detect(frame, personDetectionParameters);
		frame.release();
		return personDetections;
	}

	@Override
	public void detectPersonsFromDelta(MotionCorrelation motionCorrelation) {
		Frame frame = motionCorrelation.getFrame();

		// Only detect if there is an average frame to compare with, and we haven't run
		// person detection before for this frame.
		if (frame == null) {
			logger.warn("Cannot run person detection. Frame is null");
		} else if (frame.getPreviousFrame() == null) {
			logger.warn("Cannot run person detection for camera: " + motionCorrelation.getCamera()
					+ " and frame timestamp: " + motionCorrelation.getFrameTimestamp() + " - Previous frame is null");
		} else if (motionCorrelation.getPersonDetections() != null) {
			logger.warn("Not running person detection for camera: " + motionCorrelation.getCamera()
					+ " and frame timestamp: " + motionCorrelation.getFrameTimestamp()
					+ " - Has already been run for this frame");
		} else {
			Mat averageFrame = frame.getPreviousFrame().getAverageFrame();
			PersonDetectionParameters personDetectionParameters = new PersonDetectionParameters();
			Mat blurredFrame = frame.getBlurredFrame();

			Mat absBlurredFrame = new Mat();
			Mat absAverageFrame = new Mat();
			Core.convertScaleAbs(blurredFrame, absBlurredFrame);
			Core.convertScaleAbs(averageFrame, absAverageFrame);
			Mat frameDelta = new Mat();
			Core.absdiff(absBlurredFrame, absAverageFrame, frameDelta);
			absBlurredFrame.release();
			absAverageFrame.release();

			// Perform the person detection.
			PersonDetections personDetections = detect(frameDelta, personDetectionParameters);
			motionCorrelation.setPersonDetections(personDetections);

			Image averageImage = new Image(frame.getSequence(), frame.getTimestamp(),
					ImageUtils.encodeImage(averageFrame));
			Image delta = new Image(frame.getSequence(), frame.getTimestamp(), ImageUtils.encodeImage(frameDelta));
			frameDelta.release();
			motionCorrelation.setAverageFrame(averageImage);
			motionCorrelation.setDelta(delta);
		}
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
			List<Double> weights = new ArrayList<>();
			for (int weightIndex = 0; weightIndex < foundWeights.cols(); weightIndex++) {
				weights.add(foundWeights.get(detectionIndex, weightIndex)[0]);
			}
			personDetections.add(new PersonDetection(personDetectionParameters.getImageWidthPixels(),
					locations.get(detectionIndex), weights));
		}
		personDetections.sort(Comparator.comparing(PersonDetection::getWeight).reversed());
		foundLocations.release();
		foundWeights.release();
		return new PersonDetections(personDetections, stopWatch.getTime(TimeUnit.MILLISECONDS));
	}
}
