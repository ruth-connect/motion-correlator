package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.time.LocalDateTime;

public class PersonDetection {

	private LocalDateTime timestamp;
	private ObjectDetection frontalFaceDetection;
	private ObjectDetection profileFaceDetection;
	private ObjectDetection upperBodyDetection;
	private ObjectDetection lowerBodyDetection;
	private ObjectDetection fullBodyDetection;

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setFrontalFaceDetection(ObjectDetection frontalFaceDetection) {
		this.frontalFaceDetection = frontalFaceDetection;
	}

	public ObjectDetection getFrontalFaceDetection() {
		return frontalFaceDetection;
	}

	public void setProfileFaceDetection(ObjectDetection profileFaceDetection) {
		this.profileFaceDetection = profileFaceDetection;
	}

	public ObjectDetection getProfileFaceDetection() {
		return profileFaceDetection;
	}

	public void setUpperBodyDetection(ObjectDetection upperBodyDetection) {
		this.upperBodyDetection = upperBodyDetection;
	}

	public ObjectDetection getUpperBodyDetection() {
		return upperBodyDetection;
	}

	public void setLowerBodyDetection(ObjectDetection lowerBodyDetection) {
		this.lowerBodyDetection = lowerBodyDetection;
	}

	public ObjectDetection getLowerBodyDetection() {
		return lowerBodyDetection;
	}

	public void setFullBodyDetection(ObjectDetection fullBodyDetection) {
		this.fullBodyDetection = fullBodyDetection;
	}

	public ObjectDetection getFullBodyDetection() {
		return fullBodyDetection;
	}
}
