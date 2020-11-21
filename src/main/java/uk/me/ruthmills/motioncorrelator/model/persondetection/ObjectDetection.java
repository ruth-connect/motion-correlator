package uk.me.ruthmills.motioncorrelator.model.persondetection;

import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;

public class ObjectDetection {

	private MatOfRect objects;
	private MatOfInt rejectLevels;
	private MatOfDouble levelWeights;

	public void setObjects(MatOfRect objects) {
		this.objects = objects;
	}

	public MatOfRect getObjects() {
		return objects;
	}

	public void setRejectLevels(MatOfInt rejectLevels) {
		this.rejectLevels = rejectLevels;
	}

	public MatOfInt getRejectLevels() {
		return rejectLevels;
	}

	public void setLevelWeights(MatOfDouble levelWeights) {
		this.levelWeights = levelWeights;
	}

	public MatOfDouble getLevelWeights() {
		return levelWeights;
	}
}
