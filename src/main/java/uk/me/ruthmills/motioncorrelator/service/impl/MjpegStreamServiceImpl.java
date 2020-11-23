package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.mjpeg.Renderer;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
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
