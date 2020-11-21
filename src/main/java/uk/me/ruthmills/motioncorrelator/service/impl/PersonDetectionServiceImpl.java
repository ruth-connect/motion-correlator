package uk.me.ruthmills.motioncorrelator.service.impl;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
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

	@PostConstruct
	public void initialise() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		frontalFaceClassifier = new CascadeClassifier("haarcascade_frontalface_default.xml");
		profileFaceClassifier = new CascadeClassifier("haarcascade_profileface.xml");
		upperBodyClassifier = new CascadeClassifier("haarcascade_upperbody.xml");
		lowerBodyClassifier = new CascadeClassifier("haarcascade_lowerbody.xml");
		fullBodyClassifier = new CascadeClassifier("haarcascade_fullbody.xml");
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
