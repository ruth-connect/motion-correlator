package uk.me.ruthmills.motioncorrelator.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.service.CameraService;

@Service
public class CameraServiceImpl implements CameraService {

	private Map<String, Camera> cameras = new HashMap<>();

	@PostConstruct
	public void initialise() {

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
