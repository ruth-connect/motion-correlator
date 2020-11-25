package uk.me.ruthmills.motioncorrelator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;

@Controller
@RequestMapping("averageFrame")
public class AverageFrameController {

	@Autowired
	private AverageFrameService averageFrameService;

	@GetMapping(value = "/{camera}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getAverageFrame(@PathVariable String camera) {
		Image image = averageFrameService.getAverageFrameImage(camera);
		if (image != null) {
			return image.getBytes();
		} else {
			return null;
		}
	}
}
