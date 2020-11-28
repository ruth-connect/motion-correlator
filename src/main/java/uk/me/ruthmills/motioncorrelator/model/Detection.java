package uk.me.ruthmills.motioncorrelator.model;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;

public class Detection {

	private String camera;
	private VectorMotionDetection vectorMotionDetection;
	private PersonDetections personDetections;

	public Detection(String camera, VectorMotionDetection vectorMotionDetection, PersonDetections personDetections) {
		this.camera = camera;
		this.vectorMotionDetection = vectorMotionDetection;
		this.personDetections = personDetections;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public VectorMotionDetection getVectorMotionDetection() {
		return vectorMotionDetection;
	}

	public void setVectorMotionDetection(VectorMotionDetection vectorMotionDetection) {
		this.vectorMotionDetection = vectorMotionDetection;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}
}
