package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.TestImageService;

@Controller
@RequestMapping("/test")
public class PersonDetectionTestController {

	@Autowired
	private TestImageService testImageService;

	private final Logger logger = LoggerFactory.getLogger(PersonDetectionTestController.class);

	@GetMapping("/upload")
	public String showUploadForm(Model model) throws IOException {
		model.addAttribute("uploaded", testImageService.hasImage());
		return "upload";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes)
			throws IOException {
		logger.info("Handling file upload. Filename: " + file.getName());
		Image image = new Image();
		image.setTimestamp(LocalDateTime.now());
		image.setBytes(file.getBytes());
		testImageService.setImage(image);
		logger.info("Uploaded image.");

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/test/upload";
	}

	@GetMapping("/originalImage")
	@ResponseBody
	public byte[] getOriginalImage() {
		if (testImageService.hasImage()) {
			logger.info("Image uploaded. Returning image bytes.");
			return testImageService.getImage().getBytes();
		} else {
			logger.info("Image not uploaded.");
			return null;
		}
	}
}
