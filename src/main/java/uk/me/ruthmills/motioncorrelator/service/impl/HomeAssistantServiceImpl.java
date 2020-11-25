package uk.me.ruthmills.motioncorrelator.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;

@Service
public class HomeAssistantServiceImpl implements HomeAssistantService {

	private static final Logger logger = LoggerFactory.getLogger(HomeAssistantServiceImpl.class);

	public void notifyCameraConnected(Camera camera) {
		logger.info(camera.getName() + " connected");
	}

	public void notifyCameraConnectionFailed(Camera camera) {
		logger.info(camera.getName() + " connection failed");
	}
}
