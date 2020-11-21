package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.bitplan.mjpegstreamer.MJpegReaderRunner1;

import uk.me.ruthmills.motioncorrelator.mjpeg.MjpegRenderer;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	MjpegRenderer renderer;
	MJpegReaderRunner1 mjpegReader;

	@PostConstruct
	public void initialise() throws IOException {
		renderer = new MjpegRenderer();
		mjpegReader = new MJpegReaderRunner1();
		mjpegReader.init("http://hal9000/mjpeg_stream.php", null, null);
		mjpegReader.setViewer(renderer);
		mjpegReader.run();
	}
}
