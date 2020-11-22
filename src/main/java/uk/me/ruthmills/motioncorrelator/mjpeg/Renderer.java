package uk.me.ruthmills.motioncorrelator.mjpeg;

import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.bitplan.mjpegstreamer.MJpegReaderRunner2;
import com.bitplan.mjpegstreamer.MJpegRenderer;
import com.bitplan.mjpegstreamer.ViewerSetting;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public class Renderer implements MJpegRenderer {
	private static final int MAX_QUEUE_SIZE = 100;

	private ViewerSetting viewerSetting;
	private MJpegReaderRunner2 mjpegReader;
	private Deque<Image> images = new ConcurrentLinkedDeque<>();
	private int size;

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
	public void renderNextImage(Image image) {
		images.addLast(image);
		if (size >= MAX_QUEUE_SIZE) {
			images.removeFirst();
		} else {
			size++;
		}
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

	public Deque<Image> getImages() {
		return images;
	}
}
