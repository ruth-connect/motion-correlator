package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.ObjectDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.service.PersonDetectionService;

@Service
public class PersonDetectionServiceImpl implements PersonDetectionService {

	private CascadeClassifier frontalFaceClassifier;
	private CascadeClassifier profileFaceClassifier;
	private CascadeClassifier upperBodyClassifier;
	private CascadeClassifier lowerBodyClassifier;
	private CascadeClassifier fullBodyClassifier;

	private final Logger logger = LoggerFactory.getLogger(PersonDetectionServiceImpl.class);

	@PostConstruct
	public void initialise() {
		logger.info("Current working directory: " + new File("").getAbsolutePath());
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		frontalFaceClassifier = new CascadeClassifier("./src/main/resources/haarcascade_frontalface_default.xml");
		profileFaceClassifier = new CascadeClassifier("./src/main/resources/haarcascade_profileface.xml");
		upperBodyClassifier = new CascadeClassifier("./src/main/resources/haarcascade_upperbody.xml");
		lowerBodyClassifier = new CascadeClassifier("./src/main/resources/haarcascade_lowerbody.xml");
		fullBodyClassifier = new CascadeClassifier("./src/main/resources/haarcascade_fullbody.xml");
	}

	@Override
	public PersonDetection detectPerson(Image image) {
		Mat frame = decodeImage(image);
		PersonDetection personDetection = new PersonDetection();
		personDetection.setFrontalFaceDetection(detect(frontalFaceClassifier, frame));
		personDetection.setProfileFaceDetection(detect(profileFaceClassifier, frame));
		personDetection.setUpperBodyDetection(detect(upperBodyClassifier, frame));
		personDetection.setLowerBodyDetection(detect(lowerBodyClassifier, frame));
		personDetection.setFullBodyDetection(detect(fullBodyClassifier, frame));
		frame.release();
		return personDetection;
	}

	private Mat decodeImage(Image image) {
		Mat encoded = new Mat(1, image.getBytes().length, CvType.CV_8U);
		encoded.put(0, 0, image.getBytes());
		Mat decoded = Imgcodecs.imdecode(encoded, Imgcodecs.IMREAD_GRAYSCALE);
		encoded.release();
		return decoded;
	}

	private ObjectDetection detect(CascadeClassifier classifier, Mat frame) {
		MatOfRect objects = new MatOfRect();
		MatOfInt rejectLevels = new MatOfInt();
		MatOfDouble levelWeights = new MatOfDouble();
		classifier.detectMultiScale3(frame, objects, rejectLevels, levelWeights, 1.1, 3, 0);
		ObjectDetection objectDetection = new ObjectDetection();
		objectDetection.setObjects(objects);
		objectDetection.setRejectLevels(rejectLevels);
		objectDetection.setLevelWeights(levelWeights);
		return objectDetection;
	}
}
