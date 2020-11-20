package uk.me.ruthmills.motioncorrelator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MotionCorrelatorController {

	private final Logger logger = LoggerFactory.getLogger(MotionCorrelatorController.class);

	@RequestMapping(value = "/vectorLine", method = RequestMethod.POST)
	public void vector(@RequestParam String camera, @RequestParam String vectorLine) {
		logger.debug(camera + " : " + vectorLine);
	}
}
