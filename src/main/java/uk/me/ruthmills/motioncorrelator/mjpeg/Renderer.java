package uk.me.ruthmills.motioncorrelator.mjpeg;

import com.bitplan.mjpegstreamer.JPeg;
import com.bitplan.mjpegstreamer.MJPeg;
import com.bitplan.mjpegstreamer.MJpegRenderer;
import com.bitplan.mjpegstreamer.ViewerSetting;

public class Renderer implements MJpegRenderer {

	private ViewerSetting viewerSetting;

	@Override
	public void init() {
	}

	@Override
	public void renderNextImage(JPeg jpeg) {
	}

	@Override
	public void stop(String msg) {
	}

	@Override
	public void showMessage(String msg) {
	}

	@Override
	public ViewerSetting getViewerSetting() {
		return viewerSetting;
	}

	@Override
	public void setViewerSetting(ViewerSetting viewerSetting) {
		this.viewerSetting = viewerSetting;
	}

	@Override
	public void showProgress(MJPeg mjpeg) {
	}
}
