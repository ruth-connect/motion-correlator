package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.mjpeg.Renderer;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	private Map<String, Renderer> streams = new HashMap<>();

	@PostConstruct
	public void initialise() throws IOException {
		addCamera("hal9000");
		addCamera("themekon");
		addCamera("bigbrother");
	}

	private void addCamera(String camera) throws IOException {
		streams.put(camera, new Renderer(camera));
	}
}
