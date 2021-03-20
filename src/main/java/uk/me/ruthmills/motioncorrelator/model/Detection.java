package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;

public class Detection {

	private String camera;
	private long sequence;
	private LocalDateTime timestamp;
	private VectorMotionDetection vectorMotionDetection;
	private PersonDetections personDetections;

	public Detection(String camera, long sequence, LocalDateTime timestamp, VectorMotionDetection vectorMotionDetection,
			PersonDetections personDetections) {
		this.camera = camera;
		this.sequence = sequence;
		this.timestamp = timestamp;
		this.vectorMotionDetection = vectorMotionDetection;
		this.personDetections = personDetections;
	}

	public String getCamera() {
		return camera;
	}

	public long getSequence() {
		return sequence;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public VectorMotionDetection getVectorMotionDetection() {
		return vectorMotionDetection;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	public double getStrongestPersonDetectionWeight() {
		return personDetections.getStrongestPersonDetection().getWeight();
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Camera: " + camera + ", Sequence: " + sequence + ", Timestamp: " + timestamp + "\n");
		if (vectorMotionDetection != null) {
			stringBuilder.append("Vector Motion Detection: " + vectorMotionDetection);
		}
		if (personDetections != null) {
			stringBuilder.append("Person Detections: " + personDetections);
		}
		return stringBuilder.toString();
	}
}
