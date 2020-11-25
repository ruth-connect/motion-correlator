package uk.me.ruthmills.motioncorrelator.service;

import java.util.List;

import uk.me.ruthmills.motioncorrelator.model.Camera;

public interface CameraService {

	public Camera getCamera(String name);

	public List<Camera> getCameras();
}
