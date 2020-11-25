package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.Camera;

public interface HomeAssistantService {

	public void notifyCameraConnected(Camera camera);

	public void notifyCameraConnectionFailed(Camera camera);
}
