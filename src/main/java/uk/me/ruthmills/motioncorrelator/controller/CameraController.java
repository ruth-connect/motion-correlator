package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.service.CameraService;

@Controller
@RequestMapping("/")
public class CameraController {

	@Autowired
	CameraService cameraService;

	@GetMapping(path = "/")
	public String showHomePage(Model model) throws IOException {
		model.addAttribute("cameras", cameraService.getCameras().stream()
				.sorted(Comparator.comparing(Camera::getLocationDescription)).collect(Collectors.toList()));
		return "index";
	}

	@GetMapping(path = "/camera/{camera}")
	public String showCameraPage(Model model, @PathVariable String camera) throws IOException {
		model.addAttribute("camera", cameraService.getCamera(camera));
		return "camera";
	}
}
