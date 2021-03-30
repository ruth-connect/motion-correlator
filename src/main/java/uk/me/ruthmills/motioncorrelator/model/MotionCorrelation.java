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
	private Image averageFrame;
	private Image delta;
	private boolean roundRobin;
	private boolean processed;

	public MotionCorrelation() {
	}

	public MotionCorrelation(String camera, Frame frame, boolean roundRobin) {
		this.camera = camera;
		this.frame = frame;
		this.roundRobin = roundRobin;
		frame.setMotionCorrelation(this);
	}

	public MotionCorrelation(String camera, Frame frame, VectorMotionDetection vectorMotionDetection) {
		this.camera = camera;
		this.frame = frame;
		this.vectorMotionDetection = vectorMotionDetection;
		frame.setMotionCorrelation(this);
	}

	public String getCamera() {
		return camera;
	}

	public LocalDateTime getFrameTimestamp() {
		return frame != null ? frame.getTimestamp() : null;
	}

	public VectorMotionDetection getVectorMotionDetection() {
		return vectorMotionDetection;
	}

	public void setVectorMotionDetection(VectorMotionDetection vectorMotionDetection) {
		this.vectorMotionDetection = vectorMotionDetection;
		processed = false;
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}

	public Image getAverageFrame() {
		return averageFrame;
	}

	public void setAverageFrame(Image averageFrame) {
		this.averageFrame = averageFrame;
	}

	public Image getDelta() {
		return delta;
	}

	public void setDelta(Image delta) {
		this.delta = delta;
	}

	public boolean isRoundRobin() {
		return roundRobin;
	}

	public void setRoundRobin(boolean roundRobin) {
		this.roundRobin = roundRobin;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
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
