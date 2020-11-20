package uk.me.ruthmills.motioncorrelator.service.impl;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

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
	public void detectPerson(String camera) {
	}
}
