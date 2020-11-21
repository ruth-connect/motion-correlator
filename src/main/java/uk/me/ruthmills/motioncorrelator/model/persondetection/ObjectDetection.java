package uk.me.ruthmills.motioncorrelator.model.persondetection;

import org.opencv.core.Rect;

public class ObjectDetection {

	private Rect object;
	private int rejectLevel;
	private double levelWeight;

	public void setObject(Rect object) {
		this.object = object;
	}

	public Rect getObject() {
		return object;
	}

	public void setRejectLevel(int rejectLevel) {
		this.rejectLevel = rejectLevel;
	}

	public int getRejectLevel() {
		return rejectLevel;
	}

	public void setLevelWeight(double levelWeight) {
		this.levelWeight = levelWeight;
	}

	public double getLevelWeight() {
		return levelWeight;
	}
}
