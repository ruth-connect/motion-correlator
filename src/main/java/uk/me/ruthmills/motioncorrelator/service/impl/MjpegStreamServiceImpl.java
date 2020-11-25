package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.config.MotionCorrelatorConfig;
import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;
import uk.me.ruthmills.motioncorrelator.thread.MjpegStream;

@Service
public class MjpegStreamServiceImpl implements MjpegStreamService {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private MotionCorrelatorConfig motionCorrelatorConfig;

	private Map<String, MjpegStream> streams = new ConcurrentHashMap<>();

	@PostConstruct
	public void initialise() throws IOException {
		List<Camera> cameras = cameraService.getCameras();
		for (Camera camera : cameras) {
			MjpegStream mjpegStream = motionCorrelatorConfig.createMjpegStream(camera);
			mjpegStream.initialise();
			streams.put(camera.getName(), mjpegStream);
		}
	}

	@Override
	public Image getLatestImage(String camera) {
		return streams.get(camera).getImages().getLast();
	}

	@Override
	public Image getImage(String camera, LocalDateTime timestamp) {
		Deque<Image> images = streams.get(camera).getImages();
		Iterator<Image> iterator = images.descendingIterator();
		Image previousImage = null;
		while (iterator.hasNext()) {
			Image currentImage = iterator.next();
			if (currentImage.getTimestamp().isBefore(timestamp)) {
				if (previousImage == null) {
					return currentImage;
				}
				long timestampMillis = timestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
				long currentMillis = currentImage.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
				long previousMillis = currentImage.getTimestamp().toInstant(ZoneOffset.UTC).toEpochMilli();
				long timeDifferenceCurrent = Math.abs(timestampMillis - currentMillis);
				long timeDifferencePrevious = Math.abs(timestampMillis - previousMillis);
				if (timeDifferenceCurrent <= timeDifferencePrevious) {
					return currentImage;
				} else {
					return previousImage;
				}
			}
			previousImage = currentImage;
		}
		return previousImage;
	}
}
