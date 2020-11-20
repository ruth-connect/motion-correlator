package uk.me.ruthmills.motioncorrelator.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

	@PostMapping(path = "/vectorData/{camera}", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	@ResponseStatus(value = HttpStatus.OK)
	public void vectorData(@PathVariable String camera, @RequestBody String vectorData)
			throws UnsupportedEncodingException {
		vectorData = URLDecoder.decode(vectorData, StandardCharsets.UTF_8.name());
		vectorData = vectorData.substring(0, vectorData.length() - 1);
		logger.info(camera + " : " + vectorData);
	}
}
