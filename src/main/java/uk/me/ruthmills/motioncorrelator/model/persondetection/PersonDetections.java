package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.time.LocalDateTime;
import java.util.List;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public class PersonDetections {

	private LocalDateTime timestamp;
	private List<PersonDetection> personDetections;
	private long detectionTimeMilliseconds;
	private Image delta;

	public PersonDetections(List<PersonDetection> personDetections, long detectionTimeMilliseconds) {
		this.personDetections = personDetections;
		this.detectionTimeMilliseconds = detectionTimeMilliseconds;
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

	public void setDelta(Image delta) {
		this.delta = delta;
	}

	public Image getDelta() {
		return delta;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Timestamp: " + timestamp + "\n");
		stringBuilder.append("Person Detections: " + "\n" + personDetections + "\n");
		stringBuilder.append("Detection Time: " + detectionTimeMilliseconds + " ms");
		return stringBuilder.toString();
	}
}
