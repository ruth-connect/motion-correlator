package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.me.ruthmills.motioncorrelator.service.CameraService;

@Controller
@RequestMapping("/")
public class HomeController {

	@Autowired
	CameraService cameraService;

	@GetMapping(path = "/")
	public String showHomePage(Model model) throws IOException {
		model.addAttribute("cameras", cameraService.getCameras());
		return "index";
	}
}
