package uk.me.ruthmills.motioncorrelator.service.impl;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.service.DetectionFileService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
import uk.me.ruthmills.motioncorrelator.service.ImageFileService;
import uk.me.ruthmills.motioncorrelator.service.RemoteBackupService;

@Service
public class RemoteBackupServiceImpl implements RemoteBackupService {

	@Autowired
	private DetectionFileService detectionFileService;

	@Autowired
	private ImageFileService imageFileService;

	@Autowired
	private HomeAssistantService homeAssistantService;

	private RemoteBackup remoteBackup;

	private boolean diskOK = false;

	private static final Logger logger = LoggerFactory.getLogger(RemoteBackupServiceImpl.class);

	@Override
	public void writeDetection(Detection detection) {
		remoteBackup.addDetection(detection);
	}

	@PostConstruct
	public void initialise() {
		remoteBackup = new RemoteBackup();
		remoteBackup.initialise();
	}

	private class RemoteBackup implements Runnable {

		private BlockingDeque<Detection> remoteBackupQueue = new LinkedBlockingDeque<>();
		private Thread remoteBackupThread;

		public void initialise() {
			remoteBackupThread = new Thread(this, "Remote Backup");
			remoteBackupThread.setPriority(4); // lower priority than normal (5).
			remoteBackupThread.start();
			logger.info("Remote Backup Thread started");
		}

		public void addDetection(Detection detection) {
			remoteBackupQueue.offerLast(detection);
		}

		@Override
		public void run() {
			while (true) {
				try {
					Detection detection = remoteBackupQueue.takeFirst();
					if (detection != null) {
						logger.info("REMOTE BACKUP - backing up detection for camera: " + detection.getCamera()
								+ " with timestamp: " + detection.getTimestamp() + " and sequence: "
								+ detection.getSequence());

						// Write the images to the remote server.
						try {
							imageFileService.writeImages(detection, true);

							if (!diskOK) {
								homeAssistantService.notifyRemoteDiskWriteOK();
								diskOK = true;
							}
						} catch (Exception ex) {
							logger.info("Failed to write images", ex);
							if (diskOK) {
								homeAssistantService.notifyRemoteDiskWriteFailed();
								diskOK = false;
							}
						}

						// Clear the images to free up memory.
						detection.setImage(null);
						detection.setAverageImage(null);
						detection.setDeltaImage(null);

						// Write the detection to a JSON file on the remote server.
						try {
							detectionFileService.writeDetection(detection, true);
							if (!diskOK) {
								homeAssistantService.notifyRemoteDiskWriteOK();
								diskOK = true;
							}
						} catch (Exception ex) {
							logger.error("Failed writing detection to file", ex);
							if (diskOK) {
								homeAssistantService.notifyRemoteDiskWriteFailed();
								diskOK = false;
							}
						}
					}
				} catch (Exception ex) {
					logger.error("Failed performing remote backup", ex);
				}
			}
		}
	}
}
