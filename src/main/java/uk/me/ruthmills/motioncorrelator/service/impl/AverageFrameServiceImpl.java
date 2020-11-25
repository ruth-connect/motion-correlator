package uk.me.ruthmills.motioncorrelator.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.thread.AverageFrame;

@Service
public class AverageFrameServiceImpl implements AverageFrameService {

	@Autowired
	private CameraService cameraService;

	private Map<String, AverageFrame> averageFrames = new ConcurrentHashMap<>();

	@PostConstruct
	public void initialise() {
		List<Camera> cameras = cameraService.getCameras();
		for (Camera camera : cameras) {
			AverageFrame averageFrame = new AverageFrame(camera);
			averageFrame.initialise();
			averageFrames.put(camera.getName(), averageFrame);
		}
	}

	@Override
	public void addCurrentFrame(String camera, Image image) {
		averageFrames.get(camera).addCurrentFrame(image);
	}

	@Override
	public Image getAverageFrame(String camera) {
		return averageFrames.get(camera).getAverageFrame();
	}

	@Override
	public Mat getAverageFrameMat(String camera) {
		return averageFrames.get(camera).getAverageFrameMat();
	}
}
