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

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("centre x: " + Math.round(((float) object.tl().x + (float) object.br().x) / 2f)
				+ ", centre y: " + Math.round(((float) object.tl().y + (float) object.br().y) / 2f) + ", left: "
				+ object.tl().x + ", top: " + object.tl().y + ", right: " + object.br().x + ", bottom: " + object.br().y
				+ "\n");
		stringBuilder.append("Reject Levels: " + rejectLevels + "\n");
		stringBuilder.append("Level Weights: " + levelWeights + "\n");
		return stringBuilder.toString();
	}
}
