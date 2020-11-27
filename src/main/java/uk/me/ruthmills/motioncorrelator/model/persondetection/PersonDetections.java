package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public class PersonDetections {

	private Image image;
	private List<PersonDetection> personDetections;
	private long detectionTimeMilliseconds;
	private Image delta;

	public PersonDetections(List<PersonDetection> personDetections, long detectionTimeMilliseconds) {
		this.personDetections = personDetections;
		this.detectionTimeMilliseconds = detectionTimeMilliseconds;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
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

	public String getDetectionsFilename() {
		String detections = personDetections.size() > 0 ? "-" + personDetections.size() + "-"
				+ new BigDecimal(personDetections.get(0).getWeight()).setScale(3, RoundingMode.HALF_UP) : "";
		return image.getTimestamp() + "-stamped" + detections + ".jpg";
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Timestamp: " + image.getTimestamp() + "\n");
		stringBuilder.append("Person Detections: " + "\n" + personDetections + "\n");
		stringBuilder.append("Detection Time: " + detectionTimeMilliseconds + " ms");
		return stringBuilder.toString();
	}
}
