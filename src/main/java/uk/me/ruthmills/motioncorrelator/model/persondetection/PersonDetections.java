package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonDetections {

	private List<PersonDetection> personDetections;
	private long detectionTimeMilliseconds;

	public PersonDetections() {
	}

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

	@JsonIgnore
	public PersonDetection getStrongestPersonDetection() {
		if (personDetections == null || personDetections.size() == 0) {
			return null;
		}
		return personDetections.stream().sorted((pd1, pd2) -> Double.compare(pd2.getWeight(), pd1.getWeight()))
				.collect(Collectors.toList()).get(0);
	}

	public long getDetectionTimeMilliseconds() {
		return detectionTimeMilliseconds;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Person Detections: " + "\n" + personDetections + "\n");
		stringBuilder.append("Detection Time: " + detectionTimeMilliseconds + " ms\n");
		return stringBuilder.toString();
	}
}
