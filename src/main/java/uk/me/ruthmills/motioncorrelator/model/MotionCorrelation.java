package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;

public class MotionCorrelation {

	private String camera;
	private LocalDateTime vectorTimestamp;
	private Vector frameVector;
	private Image image;
	private PersonDetections personDetections;
	private Image stampedImage;

	public MotionCorrelation() {
	}

	public MotionCorrelation(String camera, LocalDateTime vectorTimestamp, Vector frameVector) {
		this.camera = camera;
		this.vectorTimestamp = vectorTimestamp;
		this.frameVector = frameVector;
	}

	public String getCamera() {
		return camera;
	}

	public LocalDateTime getVectorTimestamp() {
		return vectorTimestamp;
	}

	public Vector getFrameVector() {
		return frameVector;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public void setPersonDetections(PersonDetections personDetections) {
		this.personDetections = personDetections;
	}

	public PersonDetections getPersonDetections() {
		return personDetections;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Vector Timestamp: " + vectorTimestamp + "\n");
		stringBuilder.append("Frame Vector: " + frameVector + "\n");
		stringBuilder.append("Person Detection Data: " + personDetections + "\n");
		return stringBuilder.toString();
	}

	public void setStampedImage(Image stampedImage) {
		this.stampedImage = stampedImage;
	}

	public Image getStampedImage() {
		return stampedImage;
	}
}
