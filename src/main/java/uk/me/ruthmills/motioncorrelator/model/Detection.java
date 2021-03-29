package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;

public class Detection {

	private String camera;
	private long sequence;
	private LocalDateTime timestamp;
	private VectorMotionDetection vectorMotionDetection;
	private PersonDetections personDetections;
	private LocalDateTime processTime;

	public Detection() {
	}

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

	public LocalDateTime getProcessTime() {
		return processTime;
	}

	public void setProcessTime(LocalDateTime processTime) {
		this.processTime = processTime;
	}

	public VectorMotionDetection getVectorMotionDetection() {
		return vectorMotionDetection;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	@JsonIgnore
	public double getStrongestPersonDetectionWeight() {
		return personDetections != null && personDetections.getStrongestPersonDetection() != null
				? personDetections.getStrongestPersonDetection().getWeight()
				: 0d;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Detection)) {
			return false;
		} else {
			Detection detection = (Detection) obj;
			return this.getTimestamp().equals(detection.getTimestamp())
					&& this.getSequence() == detection.getSequence();
		}
	}

	@Override
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
