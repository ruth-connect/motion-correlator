package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.bitplan.mjpegstreamer.MJpegReaderRunner1;
import com.bitplan.mjpegstreamer.ViewerSetting;
import com.bitplan.mjpegstreamer.ViewerSetting.DebugMode;

import uk.me.ruthmills.motioncorrelator.mjpeg.MjpegRenderer;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	ViewerSetting viewerSetting;
	MjpegRenderer renderer;
	MJpegReaderRunner1 mjpegReader;

	@PostConstruct
	public void initialise() throws IOException {
		viewerSetting = new ViewerSetting();
		viewerSetting.setDebugMode(DebugMode.Verbose);
		renderer = new MjpegRenderer();
		renderer.setViewerSetting(viewerSetting);
		mjpegReader = new MJpegReaderRunner1();
		mjpegReader.setViewer(renderer);
		mjpegReader.init("http://hal9000/mjpeg_stream.php", null, null);
		mjpegReader.run();
	}
}
