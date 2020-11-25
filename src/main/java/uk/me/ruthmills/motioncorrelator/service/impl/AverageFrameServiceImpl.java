package uk.me.ruthmills.motioncorrelator.service.impl;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.AverageFrame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.thread.AverageFrames;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class AverageFrameServiceImpl implements AverageFrameService {

	@Autowired
	private CameraService cameraService;

	private Map<String, AverageFrames> averageFrames = new ConcurrentHashMap<>();

	@PostConstruct
	public void initialise() {
		List<Camera> cameras = cameraService.getCameras();
		for (Camera camera : cameras) {
			AverageFrames averageFrame = new AverageFrames(camera);
			averageFrame.initialise();
			averageFrames.put(camera.getName(), averageFrame);
		}
	}

	@Override
	public void addCurrentFrame(String camera, Image image) {
		averageFrames.get(camera).addCurrentFrame(image);
	}

	@Override
	public Image getAverageFrameImage(String camera) {
		AverageFrame averageFrame = averageFrames.get(camera).getAverageFrames().getLast();
		if (averageFrame != null) {
			return new Image(averageFrame.getTimestamp(), ImageUtils.encodeImage(averageFrame.getMat()));
		} else {
			return null;
		}
	}

	@Override
	public Mat getAverageFrameMat(String camera) {
		return averageFrames.get(camera).getAverageFrames().getLast().getMat();
	}

	@Override
	public Mat getAverageFrameMatBefore(String camera, LocalDateTime timestamp) {
		Iterator<AverageFrame> iterator = averageFrames.get(camera).getAverageFrames().descendingIterator();
		AverageFrame previousFrame = null;
		while (iterator.hasNext()) {
			AverageFrame currentFrame = iterator.next();
			if (currentFrame.getTimestamp().isBefore(timestamp)) {
				return currentFrame.getMat();
			} else {
				previousFrame = currentFrame;
			}
		}
		if (previousFrame != null) {
			return previousFrame.getMat();
		} else {
			return null;
		}
	}
}
