package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.mjpeg.MjpegCache;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	private MjpegCache mjpegCache;

	@PostConstruct
	public void initialise() throws IOException {
		mjpegCache = new MjpegCache("hal9000");
		mjpegCache.run();
	}
}
