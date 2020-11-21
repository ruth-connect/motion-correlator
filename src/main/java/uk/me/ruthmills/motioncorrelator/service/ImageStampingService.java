package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;

public interface ImageStampingService {
	public void stampImage(MotionCorrelation motionCorrelation) throws IOException;
}
