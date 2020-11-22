package uk.me.ruthmills.motioncorrelator.mjpeg;

import java.io.IOException;

import com.bitplan.mjpegstreamer.JPeg;
import com.bitplan.mjpegstreamer.MJPeg;
import com.bitplan.mjpegstreamer.MJpegReaderRunner2;
import com.bitplan.mjpegstreamer.MJpegRenderer;
import com.bitplan.mjpegstreamer.ViewerSetting;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

public class Renderer implements MJpegRenderer {

	private ViewerSetting viewerSetting;
	private MJpegReaderRunner2 mjpegReader;

	public Renderer(String camera) throws IOException {
		viewerSetting = new ViewerSetting();
		viewerSetting.setDebugMode(DebugMode.Verbose);
		viewerSetting.setReadTimeOut(5000);
		mjpegReader = new MJpegReaderRunner2();
		mjpegReader.setViewer(this);
		mjpegReader.init("http://" + camera + "/mjpeg_stream.php", null, null);
		mjpegReader.start();
	}

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
