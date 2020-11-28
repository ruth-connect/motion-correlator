package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;

public class MotionCorrelation {

	private String camera;
	private LocalDateTime vectorTimestamp;
	private Vector frameVector;
	private Frame frame;
	private PersonDetections personDetections;
	private Image stampedImage;

	public MotionCorrelation() {
	}

	public MotionCorrelation(String camera, Frame frame) {
		this.camera = camera;
		this.frame = frame;
	}

	public MotionCorrelation(String camera, LocalDateTime vectorTimestamp, Vector frameVector) {
		this.camera = camera;
		this.vectorTimestamp = vectorTimestamp;
		this.frameVector = frameVector;
	}

	public String getCamera() {
		return camera;
	}

	public LocalDateTime getFrameTimestamp() {
		return frame != null ? frame.getTimestamp() : null;
	}

	public LocalDateTime getVectorTimestamp() {
		return vectorTimestamp;
	}

	public Vector getFrameVector() {
		return frameVector;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public Frame getFrame() {
		return frame;
	}

	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (vectorTimestamp != null) {
			stringBuilder.append("Vector Timestamp: " + vectorTimestamp + "\n");
		}
		if (frameVector != null) {
			stringBuilder.append("Frame Vector: " + frameVector + "\n");
		}
		if (personDetections != null) {
			stringBuilder.append("Person Detection Data: " + personDetections + "\n");
		}
		return stringBuilder.toString();
	}

	public void setStampedImage(Image stampedImage) {
		this.stampedImage = stampedImage;
	}

	public Image getStampedImage() {
		return stampedImage;
	}
}
