package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;

public interface HomeAssistantService {

	public void notifyHeimdallrWatchdogPulse();

	public void notifyCameraConnected(Camera camera);

	public void notifyCameraConnectionFailed(Camera camera);

	public void notifyPersonDetected(Camera camera, PersonDetections personDetections);
}
