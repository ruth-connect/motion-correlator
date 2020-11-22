package uk.me.ruthmills.motioncorrelator.model;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;

public class MotionCorrelation {

	private VectorDataList vectorData;
	private Image image;
	private PersonDetections personDetections;
	private Image stampedImage;

	public void setVectorData(VectorDataList vectorData) {
		this.vectorData = vectorData;
	}

	public VectorDataList getVectorData() {
		return vectorData;
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
		stringBuilder.append("Vector Data: " + vectorData + "\n");
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
