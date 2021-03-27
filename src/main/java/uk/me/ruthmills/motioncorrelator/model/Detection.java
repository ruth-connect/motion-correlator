package uk.me.ruthmills.motioncorrelator.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;

public class Detection {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS z");

	private String camera;
	private long sequence;
	private LocalDateTime timestamp;
	private VectorMotionDetection vectorMotionDetection;
	private PersonDetections personDetections;

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

	@JsonIgnore
	public String getDate() {
		return timestamp.format(DATE_FORMAT);
	}

	@JsonIgnore
	public String getTime() {
		return timestamp.format(TIME_FORMAT);
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

	@JsonIgnore
	public String getStrongestPersonDetectionWeightString() {
		double strongestPersonDetectionWeight = getStrongestPersonDetectionWeight();
		if (strongestPersonDetectionWeight == 0d) {
			return "";
		}
		BigDecimal weight = new BigDecimal(strongestPersonDetectionWeight);
		return weight.setScale(3, RoundingMode.HALF_UP).toString();
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
