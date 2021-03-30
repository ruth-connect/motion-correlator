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

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.service.FrameService;

@Controller
@RequestMapping("snapshot")
public class SnapshotController {

	@Autowired
	private FrameService frameService;

	private static final Logger logger = LoggerFactory.getLogger(SnapshotController.class);

	@GetMapping(value = "/{camera}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getSnapshot(@PathVariable String camera) throws IOException {
		try {
			Frame frame = frameService.getLatestFrame(camera);
			if (frame != null) {
				return frame.getImage().getBytes();
			}
		} catch (Exception ex) {
			logger.error("Failed to get snapshot image", ex);
		}
		Path path = FileSystems.getDefault().getPath("src/main/resources", "image-not-available.jpg");
		return Files.readAllBytes(path);
	}
}
