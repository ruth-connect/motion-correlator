package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.util.List;

import org.opencv.core.Rect;

public class ObjectDetection {

	private List<Rect> objects;
	private List<Integer> rejectLevels;
	private List<Double> levelWeights;

	public void setObjects(List<Rect> objects) {
		this.objects = objects;
	}

	public List<Rect> getObjects() {
		return objects;
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
}
