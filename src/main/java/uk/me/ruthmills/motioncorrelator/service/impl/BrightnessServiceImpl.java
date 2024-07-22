package uk.me.ruthmills.motioncorrelator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.service.BrightnessService;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;

@Service
public class BrightnessServiceImpl implements BrightnessService {

	@Autowired
	private FrameService frameService;

	@Autowired
	private HomeAssistantService homeAssistantService;

	/**
	 * Notify the current overall brightness of the specified camera's image.
	 * 
	 * @param camera The camera.
	 */
	public void notifyBrightness(Camera camera) {
		if (camera.isConnected()) {
			Frame frame = frameService.getLatestFrame(camera.getName());
			if (frame != null) {
				Double brightness = frame.getBrightness();
				if (brightness != null) {
					homeAssistantService.notifyBrightness(camera, brightness);
				}
			}
		}
	}
}
