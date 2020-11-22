package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.time.LocalDateTime;
import java.util.List;

public class PersonDetections {

	private LocalDateTime timestamp;
	private List<PersonDetection> personDetections;

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

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Timestamp: " + timestamp + "\n");
		stringBuilder.append("Person Detections: " + "\n" + personDetections + "\n");
		return stringBuilder.toString();
	}
}
