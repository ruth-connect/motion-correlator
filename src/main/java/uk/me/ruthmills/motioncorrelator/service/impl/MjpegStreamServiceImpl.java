package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.bitplan.mjpegstreamer.MJpegReaderRunner1;

import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	MJpegReaderRunner1 mjpegReader;

	@PostConstruct
	public void initialise() throws IOException {
		mjpegReader = new MJpegReaderRunner1();
		mjpegReader.init("http://hal9000/mjpeg_stream.php", "", "");
		mjpegReader.run();
	}
}
