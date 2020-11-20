package uk.me.ruthmills.motioncorrelator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class MotionCorrelatorController {

	private final Logger logger = LoggerFactory.getLogger(MotionCorrelatorController.class);

	@RequestMapping(value = "/vectorLine/{camera}", method = RequestMethod.POST)
	public void vector(@PathVariable String camera, @RequestBody String vectorLine) {
		logger.debug(camera + " : " + vectorLine);
	}
}
