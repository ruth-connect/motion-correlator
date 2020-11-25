package uk.me.ruthmills.motioncorrelator.polling;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;
import uk.me.ruthmills.motioncorrelator.service.CameraService;

@Component
public class AverageFramePoller {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private AverageFrameService averageFrameService;

	private final Logger logger = LoggerFactory.getLogger(AverageFramePoller.class);

	@Scheduled(cron = "*/1 * * * * *")
	public void tick() {
		try {
			List<Camera> cameras = cameraService.getCameras();
			for (Camera camera : cameras) {
				averageFrameService.addCurrentFrame(camera.getName());
			}
		} catch (Exception ex) {
			logger.error("Exception in poller thread", ex);
		}
	}
}
