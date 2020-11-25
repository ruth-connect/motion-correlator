package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.service.CameraService;

@Service
public class CameraServiceImpl implements CameraService {

	private Map<String, Camera> cameras = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(CameraServiceImpl.class);

	@PostConstruct
	public void initialise() throws IOException {
		// Read cameras from JSON file.
		Path path = FileSystems.getDefault().getPath("src/main/resources", "cameras.json");
		String json = Files.readString(path);
		logger.info("Cameras JSON: " + json);
		logger.info("Cameras: " + getCameras().toString());
	}

	@Override
	public Camera getCamera(String name) {
		return cameras.get(name);
	}

	@Override
	public List<Camera> getCameras() {
		return cameras.entrySet().stream().map(camera -> camera.getValue()).collect(Collectors.toList());
	}
}
