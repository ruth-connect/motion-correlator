package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class PersonDetections {

	private List<PersonDetection> personDetections;
	private long detectionTimeMilliseconds;

	public PersonDetections(List<PersonDetection> personDetections, long detectionTimeMilliseconds) {
		this.personDetections = personDetections;
		this.detectionTimeMilliseconds = detectionTimeMilliseconds;
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

	public String getDetectionsFilename(long sequence, LocalDateTime timestamp) {
		String detections = personDetections.size() > 0 ? "-" + personDetections.size() + "-"
				+ new BigDecimal(personDetections.get(0).getWeight()).setScale(3, RoundingMode.HALF_UP) : "";
		return timestamp + "-" + sequence + "-" + "-stamped" + detections + ".jpg";
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Person Detections: " + "\n" + personDetections + "\n");
		stringBuilder.append("Detection Time: " + detectionTimeMilliseconds + " ms\n");
		return stringBuilder.toString();
	}
}
