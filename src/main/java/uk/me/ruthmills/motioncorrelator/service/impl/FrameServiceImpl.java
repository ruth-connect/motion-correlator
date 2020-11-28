package uk.me.ruthmills.motioncorrelator.service.impl;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.config.MotionCorrelatorConfig;
import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.thread.Frames;
import uk.me.ruthmills.motioncorrelator.thread.MjpegStream;
import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

@Service
public class FrameServiceImpl implements FrameService {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private MotionCorrelatorConfig motionCorrelatorConfig;

	private Map<String, Frames> framesMap = new ConcurrentHashMap<>();
	private Map<String, MjpegStream> streamsMap = new ConcurrentHashMap<>();

	@PostConstruct
	public void initialise() {
		List<Camera> cameras = cameraService.getCameras();
		for (Camera camera : cameras) {
			Frames frames = new Frames(camera);
			MjpegStream stream = motionCorrelatorConfig.createMjpegStream(camera);

			frames.initialise();
			stream.initialise();

			framesMap.put(camera.getName(), frames);
			streamsMap.put(camera.getName(), stream);
		}
	}

	@Override
	public void addCurrentFrame(String camera, Image image) {
		framesMap.get(camera).addCurrentFrame(image);
	}

	@Override
	public Frame getLatestFrame(String camera) {
		try {
			return framesMap.get(camera).getFrames().getLast();
		} catch (NoSuchElementException ex) {
			return null;
		}
	}

	@Override
	public Frame getFrame(String camera, LocalDateTime timestamp) {
		Deque<Frame> frames = framesMap.get(camera).getFrames();
		Iterator<Frame> iterator = frames.descendingIterator();
		Frame previousFrame = null;
		while (iterator.hasNext()) {
			Frame currentFrame = iterator.next();
			if (currentFrame.getTimestamp().isBefore(timestamp)) {
				if (previousFrame == null) {
					return currentFrame;
				}
				long timestampMillis = TimeUtils.toMilliseconds(timestamp);
				long currentMillis = TimeUtils.toMilliseconds(currentFrame.getTimestamp());
				long previousMillis = TimeUtils.toMilliseconds(currentFrame.getTimestamp());
				long timeDifferenceCurrent = Math.abs(timestampMillis - currentMillis);
				long timeDifferencePrevious = Math.abs(timestampMillis - previousMillis);
				if (timeDifferenceCurrent <= timeDifferencePrevious) {
					return currentFrame;
				} else {
					return previousFrame;
				}
			}
			previousFrame = currentFrame;
		}
		return previousFrame;
	}
}
