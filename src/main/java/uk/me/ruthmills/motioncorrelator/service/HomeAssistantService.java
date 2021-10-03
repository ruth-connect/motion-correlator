package uk.me.ruthmills.motioncorrelator.service;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;

public interface HomeAssistantService {

	public void notifyHeimdallrWatchdogPulse();

	public void notifyCameraConnected(Camera camera);

	public void notifyCameraConnectionFailed(Camera camera);

	public void notifyCameraStreamBehindSchedule(Camera camera);

	public void notifyDiskWriteOK();

	public void notifyDiskWriteFailed();

	public void notifyDiskSpaceFreeStart();

	public void notifyDiskSpaceFreed();

	public void notifyDiskSpaceNotFreed();

	public void notifyRemoteDiskSpaceFreeStart();

	public void notifyRemoteDiskWriteOK();

	public void notifyRemoteDiskWriteFailed();

	public void notifyRemoteDiskSpaceFreed();

	public void notifyRemoteDiskSpaceNotFreed();

	public void notifyMediaDiskSpaceUsed(String mediaDiskSpace);

	public void notifyRemoteDiskSpaceUsed(String remoteDiskSpace);

	public void notifyPersonDetected(Camera camera, long sequence, LocalDateTime timestamp,
			PersonDetections personDetections);
}
