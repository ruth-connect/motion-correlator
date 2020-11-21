package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
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
	public void initialise() throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String currentWorkingDirectory = new File("").getAbsolutePath();
		logger.info("Current working directory: " + currentWorkingDirectory);

		frontalFaceClassifier = createClassifier(
				currentWorkingDirectory + "/src/main/resources/haarcascade_frontalface_default.xml");
		profileFaceClassifier = createClassifier(
				currentWorkingDirectory + "/src/main/resources/haarcascade_profileface.xml");
		upperBodyClassifier = createClassifier(
				currentWorkingDirectory + "/src/main/resources/haarcascade_upperbody.xml");
		lowerBodyClassifier = createClassifier(
				currentWorkingDirectory + "/src/main/resources/haarcascade_lowerbody.xml");
		fullBodyClassifier = createClassifier(currentWorkingDirectory + "/src/main/resources/haarcascade_fullbody.xml");
	}

	@Override
	public PersonDetection detectPerson(Image image) {
		Mat frame = decodeImage(image);
		logger.info("Frame size: " + frame.size());
		PersonDetection personDetection = new PersonDetection();
		personDetection.setFrontalFaceDetection(detect(frontalFaceClassifier, frame));
		personDetection.setProfileFaceDetection(detect(profileFaceClassifier, frame));
		personDetection.setUpperBodyDetection(detect(upperBodyClassifier, frame));
		personDetection.setLowerBodyDetection(detect(lowerBodyClassifier, frame));
		personDetection.setFullBodyDetection(detect(fullBodyClassifier, frame));
		frame.release();
		return personDetection;
	}

	private CascadeClassifier createClassifier(String filename) throws IOException {
		CascadeClassifier classifier = new CascadeClassifier(filename);
		logger.info("About to load classifier: " + filename);
		classifier.load(filename);
		if (classifier.empty()) {
			throw new IOException("Failed to load: " + filename);
		}
		return classifier;
	}

	private Mat decodeImage(Image image) {
		logger.info("Image length in bytes: " + image.getBytes().length);
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
		classifier.detectMultiScale3(frame, objects, rejectLevels, levelWeights, 1.1, 3, 0, new Size(), new Size(),
				true);
		ObjectDetection objectDetection = new ObjectDetection();
		objectDetection.setObjects(objects.toList());
		objectDetection.setRejectLevels(rejectLevels.toList());
		objectDetection.setLevelWeights(levelWeights.toList());
		logger.info("Number of detections: " + objectDetection.getObjects().size());
		return objectDetection;
	}
}
