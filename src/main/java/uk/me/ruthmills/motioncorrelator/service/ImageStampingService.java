package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface ImageStampingService {
	public Image stampImage(MotionCorrelation motionCorrelation) throws IOException;

	public byte[] stampImage(Detection detection, byte[] jpeg) throws IOException;
}
