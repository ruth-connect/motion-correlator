package uk.me.ruthmills.motioncorrelator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/")
public class MotionCorrelatorController {

	private final Logger logger = LoggerFactory.getLogger(MotionCorrelatorController.class);

	@PostMapping(value = "/vectorLine/{camera}")
	@ResponseStatus(value = HttpStatus.OK)
	public void vector(@PathVariable String camera, @RequestBody String vectorLine) {
		logger.debug(camera + " : " + vectorLine);
	}
}
