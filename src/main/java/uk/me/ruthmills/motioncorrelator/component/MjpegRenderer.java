package uk.me.ruthmills.motioncorrelator.component;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bitplan.mjpegstreamer.MJpegRenderer;
import com.bitplan.mjpegstreamer.ViewerSetting;

import uk.me.ruthmills.motioncorrelator.service.impl.MotionCorrelatorServiceImpl;

@Component
public class MjpegRenderer implements MJpegRenderer {
	private ViewerSetting viewerSetting;

	private final Logger logger = LoggerFactory.getLogger(MotionCorrelatorServiceImpl.class);

	@Override
	public ViewerSetting getViewerSetting() {
		return viewerSetting;
	}

	@Override
	public void init() {
		logger.info("init() called");
	}

	@Override
	public void renderNextImage(BufferedImage image) {
		logger.info("renderNextImage() called");
	}

	@Override
	public void setViewerSetting(ViewerSetting viewerSetting) {
		this.viewerSetting = viewerSetting;
	}

	@Override
	public void showMessage(String message) {
		logger.info("showMessage() called with message: " + message);
	}

	@Override
	public void stop(String message) {
		logger.info("stop() called with message: " + message);
	}
}
