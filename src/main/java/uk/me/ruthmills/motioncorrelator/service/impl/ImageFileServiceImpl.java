package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.ImageFileService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class ImageFileServiceImpl implements ImageFileService {

	@Value("${filesystem.media}")
	private String mediaPath;

	@Value("${filesystem.remote}")
	private String remotePath;

	private static final Logger logger = LoggerFactory.getLogger(ImageFileServiceImpl.class);

	@Override
	public void writeImage(String camera, Image image, boolean remote) throws IOException {
		writeImage(camera, image, "", remote);
	}

	@Override
	public void writeImage(String camera, Image image, String suffix, boolean remote) throws IOException {
		if (image != null) {
			Path path = FileSystems.getDefault().getPath(getImagePath(camera, image.getTimestamp(), remote),
					image.getTimestamp() + "-" + image.getSequence() + suffix + ".jpg");
			File file = path.toFile();
			if (!file.exists()) {
				Files.write(path, image.getBytes(), StandardOpenOption.CREATE);
			} else {
				logger.info("Image: " + file.getAbsolutePath() + " exists so not writing it again");
			}
		}
	}

	@Override
	public byte[] readImage(String camera, String year, String month, String day, String hour, String filename)
			throws IOException {
		String imagePath = getImagePathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour;
		logger.info("Image path: " + imagePath + "/" + filename);
		byte[] image = Files.readAllBytes(FileSystems.getDefault().getPath(imagePath, filename));
		logger.info("Image size: " + image.length + " bytes");
		return image;
	}

	private String getImagePathPrefix(boolean remote) {
		return (remote ? remotePath : mediaPath) + "/images/";
	}

	private String getImagePath(String camera, LocalDateTime timestamp, boolean remote) {
		String path = getImagePathPrefix(remote) + ImageUtils.getImagePath(camera, timestamp);
		File file = new File(path);
		file.mkdirs();
		return path;
	}
}
