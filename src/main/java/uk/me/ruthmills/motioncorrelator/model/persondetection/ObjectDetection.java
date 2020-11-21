package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.util.List;

import org.opencv.core.Rect;

public class ObjectDetection {

	private Rect object;
	private List<Integer> rejectLevels;
	private List<Double> levelWeights;

	public void setObject(Rect object) {
		this.object = object;
	}

	public Rect getObject() {
		return object;
	}

	public void setRejectLevels(List<Integer> rejectLevels) {
		this.rejectLevels = rejectLevels;
	}

	public List<Integer> getRejectLevels() {
		return rejectLevels;
	}

	public void setLevelWeights(List<Double> levelWeights) {
		this.levelWeights = levelWeights;
	}

	public List<Double> getLevelWeights() {
		return levelWeights;
	}

	public int getCentreX() {
		return (int) Math.round((object.tl().x + object.br().x) / 2d);
	}

	public int getCentreY() {
		return (int) Math.round((object.tl().y + object.br().y) / 2d);
	}

	public int getLeft() {
		return (int) Math.round(object.tl().x);
	}

	public int getTop() {
		return (int) Math.round(object.tl().y);
	}

	public int getRight() {
		return (int) Math.round(object.br().x);
	}

	public int getBottom() {
		return (int) Math.round(object.br().y);
	}

	public int getWidth() {
		return object.width;
	}

	public int getHeight() {
		return object.height;
	}

	public int getRejectLevel() {
		return rejectLevels.get(0);
	}

	public double getLevelWeight() {
		return levelWeights.get(0);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("centre x: " + getCentreX());
		stringBuilder.append(", centre y: " + getCentreY());
		stringBuilder.append(", left: " + getLeft());
		stringBuilder.append(", top: " + getTop());
		stringBuilder.append(", right: " + getRight());
		stringBuilder.append(", bottom: " + getBottom());
		stringBuilder.append(", width: " + getWidth());
		stringBuilder.append(", height: " + getHeight());
		stringBuilder.append(", rejectLevel: " + getRejectLevel());
		stringBuilder.append(", levelWeight: " + getLevelWeight() + "\n");
		return stringBuilder.toString();
	}
}
