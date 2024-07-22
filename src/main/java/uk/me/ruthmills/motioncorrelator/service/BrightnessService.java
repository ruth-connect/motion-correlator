package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.Camera;

/**
 * Service to notify the overall brightness of the current frame to Home
 * Assistant.
 * 
 * @author ruth
 *
 */
public interface BrightnessService {

	/**
	 * Notify the current overall brightness of the specified camera's image.
	 * 
	 * @param camera The camera.
	 */
	public void notifyBrightness(Camera camera);
}
