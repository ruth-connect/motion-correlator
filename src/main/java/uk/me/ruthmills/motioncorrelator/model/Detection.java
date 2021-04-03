package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;

public class Detection {

	private String camera;
	private long sequence;
	private LocalDateTime timestamp;
	private AlarmState alarmState;
	private VectorMotionDetection vectorMotionDetection;
	private PersonDetections personDetections;
	private boolean roundRobin;
	private LocalDateTime processTime;
	private byte[] image;
	private byte[] averageImage;
	private byte[] deltaImage;
	private String videoPath;

	public Detection() {
	}

	public Detection(String camera, long sequence, LocalDateTime timestamp, AlarmState alarmState,
			VectorMotionDetection vectorMotionDetection, PersonDetections personDetections, boolean roundRobin,
			byte[] image, byte[] averageImage, byte[] deltaImage) {
		this.camera = camera;
		this.sequence = sequence;
		this.timestamp = timestamp;
		this.alarmState = alarmState;
		this.vectorMotionDetection = vectorMotionDetection;
		this.personDetections = personDetections;
		this.roundRobin = roundRobin;
		this.image = image;
		this.averageImage = averageImage;
		this.deltaImage = deltaImage;
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

	public AlarmState getAlarmState() {
		return alarmState;
	}

	public boolean isRoundRobin() {
		return roundRobin;
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

	@JsonIgnore
	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	@JsonIgnore
	public byte[] getAverageImage() {
		return averageImage;
	}

	public void setAverageImage(byte[] averageImage) {
		this.averageImage = averageImage;
	}

	@JsonIgnore
	public byte[] getDeltaImage() {
		return deltaImage;
	}

	public void setDeltaImage(byte[] deltaImage) {
		this.deltaImage = deltaImage;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
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
		stringBuilder.append("Camera: " + camera + ", Sequence: " + sequence + ", Timestamp: " + timestamp
				+ ", Alarm State: " + alarmState + "\n");
		if (vectorMotionDetection != null) {
			stringBuilder.append("Vector Motion Detection: " + vectorMotionDetection);
		}
		if (personDetections != null) {
			stringBuilder.append("Person Detections: " + personDetections);
		}
		return stringBuilder.toString();
	}
}
