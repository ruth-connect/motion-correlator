package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.service.DetectionFileService;
import uk.me.ruthmills.motioncorrelator.service.ImageFileService;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;

@Controller
@RequestMapping("stamped")
public class StampedImageController {

	@Autowired
	private DetectionFileService detectionFileService;

	@Autowired
	private ImageFileService imageFileService;

	@Autowired
	private ImageStampingService imageStampingService;

	private static final Logger logger = LoggerFactory.getLogger(StampedImageController.class);

	@GetMapping(path = "/{camera}/{year}/{month}/{day}/{hour}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable String camera, @PathVariable String year, @PathVariable String month,
			@PathVariable String day, @PathVariable String hour, @PathVariable String filename) throws IOException {
		try {
			String[] filenameParts = filename.substring(0, filename.lastIndexOf(".")).split("-");
			String timestamp = filenameParts[0] + "-" + filenameParts[1] + "-" + filenameParts[2];
			String sequence = filenameParts[3];
			Detection detection = detectionFileService.readDetection(camera, year, month, day, hour, timestamp,
					sequence);
			byte[] jpeg = imageFileService.readImage(camera, year, month, day, hour, filename);
			return imageStampingService.stampImage(detection, jpeg);
		} catch (Exception ex) {
			logger.error("Failed to read stamped image", ex);
		}
		Path path = FileSystems.getDefault().getPath("src/main/resources", "image-not-available.jpg");
		return Files.readAllBytes(path);
	}
}
