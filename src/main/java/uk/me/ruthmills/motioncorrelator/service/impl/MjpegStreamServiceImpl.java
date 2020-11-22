package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.bitplan.mjpegstreamer.MJpegReaderRunner2;
import com.bitplan.mjpegstreamer.ViewerSetting;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

import uk.me.ruthmills.motioncorrelator.mjpeg.Renderer;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	private ViewerSetting viewerSetting;
	private Renderer renderer;
	private MJpegReaderRunner2 mjpegReader;

	@PostConstruct
	public void initialise() throws IOException {
		viewerSetting = new ViewerSetting();
		viewerSetting.setDebugMode(DebugMode.Verbose);
		renderer = new Renderer();
		renderer.setViewerSetting(viewerSetting);
		mjpegReader = new MJpegReaderRunner2();
		mjpegReader.setViewer(renderer);
		mjpegReader.init("http://hal9000/mjpeg_stream.php", null, null);
		mjpegReader.start();
	}
}
