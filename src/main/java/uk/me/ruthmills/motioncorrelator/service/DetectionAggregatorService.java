package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.Detections;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;

public interface DetectionAggregatorService {

	public void addDetection(MotionCorrelation motionCorrelation);

	public Detections getDetections(String camera);
}
