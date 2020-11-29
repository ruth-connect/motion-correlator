package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.ImageFileWritingService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class ImageFileWritingServiceImpl implements ImageFileWritingService {

	private static final String IMAGE_PATH_PREFIX = "/mnt/media/motioncorrelator/";

	@Override
	public void writeImage(String camera, Image image) throws IOException {
		writeImage(camera, image, "");
	}

	@Override
	public void writeImage(String camera, Image image, PersonDetections personDetections) throws IOException {
		Files.write(
				FileSystems.getDefault().getPath(getImagePath(camera, image.getTimestamp()),
						personDetections.getDetectionsFilename(image.getSequence(), image.getTimestamp())),
				image.getBytes(), StandardOpenOption.CREATE);
	}

	@Override
	public void writeImage(String camera, Image image, String suffix) throws IOException {
		if (image != null) {
			Files.write(
					FileSystems.getDefault().getPath(getImagePath(camera, image.getTimestamp()),
							image.getTimestamp() + "-" + image.getSequence() + suffix + ".jpg"),
					image.getBytes(), StandardOpenOption.CREATE);
		}
	}

	private String getImagePath(String camera, LocalDateTime timestamp) {
		String path = IMAGE_PATH_PREFIX + ImageUtils.getImagePath(camera, timestamp);
		File file = new File(path);
		file.mkdirs();
		return path;
	}
}
