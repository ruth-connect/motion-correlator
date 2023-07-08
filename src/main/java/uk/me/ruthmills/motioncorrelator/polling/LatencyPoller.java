package uk.me.ruthmills.motioncorrelator.polling;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;

/**
 * Reports the current latency stats to Home Assistant every minute.
 */
@Component
public class LatencyPoller {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private HomeAssistantService homeAssistantService;

	private static final Logger logger = LoggerFactory.getLogger(LatencyPoller.class);

	@Scheduled(cron = "0 */1 * * * *")
	public void tick() {
		try {
			List<Camera> cameras = cameraService.getCameras();
			for (Camera camera : cameras) {
				homeAssistantService.notifyLatency(camera);
			}
		} catch (Exception ex) {
			logger.error("Exception in Latency poller thread", ex);
		}
	}
}
