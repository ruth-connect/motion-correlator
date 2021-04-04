package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.me.ruthmills.motioncorrelator.service.VideoService;

@Controller
@RequestMapping("video")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@GetMapping(path = "/{camera}/{year}/{month}/{day}/{filename}", produces = "video/mp4")
	@ResponseBody
	public byte[] getVideo(@PathVariable String camera, @PathVariable String year, @PathVariable String month,
			@PathVariable String day, @PathVariable String filename) throws IOException {
		return videoService.getVideo(camera, year, month, day, filename);
	}
}
