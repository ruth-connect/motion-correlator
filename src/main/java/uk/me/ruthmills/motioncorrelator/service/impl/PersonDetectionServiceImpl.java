package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.PersonDetectionService;

@Service
public class PersonDetectionServiceImpl implements PersonDetectionService {

	private HOGDescriptor hogDescriptor;

	private final Logger logger = LoggerFactory.getLogger(PersonDetectionServiceImpl.class);

	@PostConstruct
	public void initialise() throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		hogDescriptor = new HOGDescriptor();
		hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
	}

	@Override
	public PersonDetections detectPersons(Image image) {
		Mat frame = decodeImage(image);
		logger.info("Frame size: " + frame.size());
		PersonDetections personDetections = new PersonDetections();
		personDetections.setTimestamp(image.getTimestamp());
		personDetections.setPersonDetections(detect(frame));
		frame.release();
		return personDetections;
	}

	private Mat decodeImage(Image image) {
		logger.info("Image length in bytes: " + image.getBytes().length);
		Mat encoded = new Mat(1, image.getBytes().length, CvType.CV_8U);
		encoded.put(0, 0, image.getBytes());
		Mat decoded = Imgcodecs.imdecode(encoded, Imgcodecs.IMREAD_COLOR);
		encoded.release();
		Mat resized = new Mat();
		Size size = new Size(400, 300);
		Imgproc.resize(decoded, resized, size);
		decoded.release();
		return resized;
	}

	private List<PersonDetection> detect(Mat frame) {
		MatOfRect foundLocations = new MatOfRect();
		MatOfDouble foundWeights = new MatOfDouble();
		double hitThreshold = -1.0d;
		Size winStride = new Size(4, 4);
		Size padding = new Size(8, 8);
		double scale = 1.05d;
		hogDescriptor.detectMultiScale(frame, foundLocations, foundWeights, hitThreshold, winStride, padding, scale);
		List<Rect> locations = foundLocations.toList();
		List<PersonDetection> personDetections = new ArrayList<>();
		for (int detectionIndex = 0; detectionIndex < locations.size(); detectionIndex++) {
			PersonDetection personDetection = new PersonDetection();
			personDetection.setLocation(locations.get(detectionIndex));
			List<Double> weights = new ArrayList<>();
			for (int weightIndex = 0; weightIndex < foundWeights.cols(); weightIndex++) {
				weights.add(foundWeights.get(detectionIndex, weightIndex)[0]);
			}
			personDetection.setWeights(weights);
			personDetections.add(personDetection);
		}
		logger.info("Number of person detections: " + personDetections.size());
		foundLocations.release();
		foundWeights.release();
		return personDetections;
	}
}
