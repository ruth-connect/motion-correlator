package uk.me.ruthmills.motioncorrelator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Controller
@RequestMapping("averageFrame")
public class AverageFrameController {

	@Autowired
	private FrameService frameService;

	@GetMapping(value = "/{camera}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getAverageFrame(@PathVariable String camera) {
		Frame frame = frameService.getLatestFrame(camera);
		if (frame != null) {
			return ImageUtils.encodeImage(frame.getAverageFrame());
		} else {
			return null;
		}
	}
}
