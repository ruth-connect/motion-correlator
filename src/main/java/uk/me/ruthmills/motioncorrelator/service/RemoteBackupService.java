package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.Detection;

public interface RemoteBackupService {

	public void writeDetection(Detection detection);
}
