package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.net.URISyntaxException;

public interface MotionCorrelatorService {

	public void correlateMotionAndPersonDetection(String camera, String vectorData)
			throws IOException, URISyntaxException;
}
