package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.time.LocalDateTime;
import java.util.List;

public class PersonDetection {

	private LocalDateTime timestamp;
	private List<ObjectDetection> frontalFaceDetections;
	private List<ObjectDetection> profileFaceDetections;
	private List<ObjectDetection> upperBodyDetections;
	private List<ObjectDetection> lowerBodyDetections;
	private List<ObjectDetection> fullBodyDetections;

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setFrontalFaceDetections(List<ObjectDetection> frontalFaceDetections) {
		this.frontalFaceDetections = frontalFaceDetections;
	}

	public List<ObjectDetection> getFrontalFaceDetections() {
		return frontalFaceDetections;
	}

	public void setProfileFaceDetections(List<ObjectDetection> profileFaceDetections) {
		this.profileFaceDetections = profileFaceDetections;
	}

	public List<ObjectDetection> getProfileFaceDetections() {
		return profileFaceDetections;
	}

	public void setUpperBodyDetections(List<ObjectDetection> upperBodyDetections) {
		this.upperBodyDetections = upperBodyDetections;
	}

	public List<ObjectDetection> getUpperBodyDetections() {
		return upperBodyDetections;
	}

	public void setLowerBodyDetections(List<ObjectDetection> lowerBodyDetections) {
		this.lowerBodyDetections = lowerBodyDetections;
	}

	public List<ObjectDetection> getLowerBodyDetections() {
		return lowerBodyDetections;
	}

	public void setFullBodyDetections(List<ObjectDetection> fullBodyDetections) {
		this.fullBodyDetections = fullBodyDetections;
	}

	public List<ObjectDetection> getFullBodyDetections() {
		return fullBodyDetections;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Timestamp: " + timestamp + "\n");
		stringBuilder.append("Frontal Face Detections: " + "\n" + frontalFaceDetections);
		stringBuilder.append("Profile Face Detections: " + "\n" + profileFaceDetections);
		stringBuilder.append("Upper Body Detections: " + "\n" + upperBodyDetections);
		stringBuilder.append("Lower Body Detections: " + "\n" + lowerBodyDetections);
		stringBuilder.append("Full Body Detections: " + "\n" + fullBodyDetections);
		return stringBuilder.toString();
	}
}
