package uk.me.ruthmills.motioncorrelator.mjpeg;

import java.io.IOException;

import com.bitplan.mjpegstreamer.MJpegReaderRunner2;
import com.bitplan.mjpegstreamer.ViewerSetting;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

public class MjpegCache {

	private ViewerSetting viewerSetting;
	private MjpegRenderer mjpegRenderer;
	private MJpegReaderRunner2 mjpegReader;

	public MjpegCache(String camera) throws IOException {
		viewerSetting = new ViewerSetting();
		viewerSetting.setDebugMode(DebugMode.Verbose);
		mjpegRenderer = new MjpegRenderer();
		mjpegRenderer.setViewerSetting(viewerSetting);
		mjpegReader = new MJpegReaderRunner2();
		mjpegReader.setViewer(mjpegRenderer);
		mjpegReader.init("http://" + camera + "/mjpeg_stream.php", null, null);
	}

	public void run() {
		mjpegReader.run();
	}
}
