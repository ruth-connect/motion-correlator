package uk.me.ruthmills.motioncorrelator.service.impl;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.config.MotionCorrelatorConfig;
import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.thread.MjpegStream;
import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

@Service
public class FrameServiceImpl implements FrameService {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private MotionCorrelatorConfig motionCorrelatorConfig;

	private Map<String, MjpegStream> streamsMap = new ConcurrentHashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(FrameServiceImpl.class);

	@PostConstruct
	public void initialise() {
		List<Camera> cameras = cameraService.getCameras();
		for (Camera camera : cameras) {
			MjpegStream stream = motionCorrelatorConfig.createMjpegStream(camera);

			stream.initialise();

			streamsMap.put(camera.getName(), stream);
		}
	}

	@Override
	public Frame getLatestFrame(String camera) {
		try {
			MjpegStream mjpegStream = streamsMap.get(camera);
			if (mjpegStream != null) {
				Deque<Frame> frames = mjpegStream.getFrames();
				if (frames != null) {
					if (!frames.isEmpty()) {
						return frames.getLast();
					}
				}
			}
			return null;
		} catch (Exception ex) {
			logger.error("Could not get latest frame for camera: " + camera, ex);
			return null;
		}
	}

	@Override
	public Frame getFrame(String camera, LocalDateTime timestamp) {
		Deque<Frame> frames = streamsMap.get(camera).getFrames();
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
				long previousMillis = TimeUtils.toMilliseconds(previousFrame.getTimestamp());
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
