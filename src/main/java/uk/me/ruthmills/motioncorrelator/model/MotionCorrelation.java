package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;

public class MotionCorrelation {

	private String camera;
	private Frame frame;
	private VectorMotionDetection vectorMotionDetection;
	private PersonDetections personDetections;
	private Image delta;

	public MotionCorrelation() {
	}

	public MotionCorrelation(String camera, Frame frame) {
		this.camera = camera;
		this.frame = frame;
	}

	public MotionCorrelation(String camera, VectorMotionDetection vectorMotionDetection) {
		this.camera = camera;
		this.vectorMotionDetection = vectorMotionDetection;
	}

	public String getCamera() {
		return camera;
	}

	public LocalDateTime getFrameTimestamp() {
		return frame != null ? frame.getTimestamp() : null;
	}

	public void setVectorMotionDetection(VectorMotionDetection vectorMotionDetection) {
		this.vectorMotionDetection = vectorMotionDetection;
	}

	public VectorMotionDetection getVectorMotionDetection() {
		return vectorMotionDetection;
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

	public void setDelta(Image delta) {
		this.delta = delta;
	}

	public Image getDelta() {
		return delta;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (vectorMotionDetection != null) {
			stringBuilder.append("Vector Motion Detection: " + vectorMotionDetection + "\n");
		}
		if (personDetections != null) {
			stringBuilder.append("Person Detections: " + personDetections + "\n");
		}
		return stringBuilder.toString();
	}
}
