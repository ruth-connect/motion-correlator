package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.time.LocalDateTime;
import java.util.List;

public class PersonDetections {

	private LocalDateTime timestamp;
	private List<PersonDetection> personDetections;
	private long detectionTimeMilliseconds;

	public PersonDetections(List<PersonDetection> personDetections, long detectionTimeMilliseconds) {
		this.personDetections = personDetections;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setPersonDetections(List<PersonDetection> personDetections) {
		this.personDetections = personDetections;
	}

	public List<PersonDetection> getPersonDetections() {
		return personDetections;
	}

	public long getDetectionTimeMilliseconds() {
		return detectionTimeMilliseconds;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Timestamp: " + timestamp + "\n");
		stringBuilder.append("Person Detections: " + "\n" + personDetections + "\n");
		stringBuilder.append("Detection Time: " + detectionTimeMilliseconds + " ms");
		return stringBuilder.toString();
	}
}
