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

@Controller
@RequestMapping("snapshot")
public class SnapshotController {

	@Autowired
	private FrameService frameService;

	@GetMapping(value = "/{camera}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getSnapshot(@PathVariable String camera) {
		Frame frame = frameService.getLatestFrame(camera);
		if (frame != null) {
			return frame.getImage().getBytes();
		} else {
			return null;
		}
	}
}
