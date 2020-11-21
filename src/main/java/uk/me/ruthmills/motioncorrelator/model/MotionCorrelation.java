package uk.me.ruthmills.motioncorrelator.model;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;

public class MotionCorrelation {

	private VectorDataList vectorData;
	private Image image;
	private PersonDetection personDetection;

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

	public void setPersonDetection(PersonDetection personDetection) {
		this.personDetection = personDetection;
	}

	public PersonDetection getPersonDetection() {
		return personDetection;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Vector Data: " + vectorData + "\n");
		stringBuilder.append("Person Detection Data: " + personDetection + "\n");
		return stringBuilder.toString();
	}
}
