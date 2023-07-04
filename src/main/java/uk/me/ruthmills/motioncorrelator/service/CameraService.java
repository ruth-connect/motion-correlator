package uk.me.ruthmills.motioncorrelator.service;

import java.util.List;

import uk.me.ruthmills.motioncorrelator.model.Camera;

/**
 * Service to manage the cameras that this application watches.
 * 
 * @author ruth
 */
public interface CameraService {

	/**
	 * Get a camera by name.
	 * 
	 * @param name The name of the camera.
	 * @return The camera.
	 */
	public Camera getCamera(String name);

	/**
	 * Get the list of all the cameras.
	 * 
	 * @return List of all the cameras.
	 */
	public List<Camera> getCameras();
}
