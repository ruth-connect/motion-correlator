package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.me.ruthmills.motioncorrelator.service.ImageFileService;

@Controller
@RequestMapping("images")
public class ImageController {

	@Autowired
	private ImageFileService imageFileService;

	@GetMapping(path = "/{camera}/{year}/{month}/{day}/{hour}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(@PathVariable String camera, @PathVariable String year, @PathVariable String month,
			@PathVariable String day, @PathVariable String hour, @PathVariable String filename) throws IOException {
		return imageFileService.readImage(camera, year, month, day, hour, filename);
	}
}
