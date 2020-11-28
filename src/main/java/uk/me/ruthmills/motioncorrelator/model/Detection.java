package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;

public class Detection {

	private String camera;
	private LocalDateTime frameTimestamp;
	private LocalDateTime vectorTimestamp;
	private Vector frameVector;
	private PersonDetections personDetections;

	public Detection(String camera, LocalDateTime frameTimestamp, LocalDateTime vectorTimestamp, Vector frameVector,
			PersonDetections personDetections) {
		this.camera = camera;
		this.frameTimestamp = frameTimestamp;
		this.vectorTimestamp = vectorTimestamp;
		this.frameVector = frameVector;
		this.personDetections = personDetections;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public LocalDateTime getFrameTimestamp() {
		return frameTimestamp;
	}

	public void setFrameTimestamp(LocalDateTime frameTimestamp) {
		this.frameTimestamp = frameTimestamp;
	}

	public LocalDateTime getVectorTimestamp() {
		return vectorTimestamp;
	}

	public void setVectorTimestamp(LocalDateTime vectorTimestamp) {
		this.vectorTimestamp = vectorTimestamp;
	}

	public Vector getFrameVector() {
		return frameVector;
	}

	public void setFrameVector(Vector frameVector) {
		this.frameVector = frameVector;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}
}
