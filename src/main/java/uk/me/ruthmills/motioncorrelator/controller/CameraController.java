package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.Detections;
import uk.me.ruthmills.motioncorrelator.service.CameraService;
import uk.me.ruthmills.motioncorrelator.service.DetectionAggregatorService;
import uk.me.ruthmills.motioncorrelator.service.DetectionFileService;

@Controller
@RequestMapping("/")
public class CameraController {

	@Autowired
	private CameraService cameraService;

	@Autowired
	private DetectionFileService detectionFileService;

	@Autowired
	private DetectionAggregatorService detectionAggregatorService;

	@GetMapping(path = "/")
	public String showHomePage(Model model) throws IOException {
		model.addAttribute("cameras", cameraService.getCameras().stream()
				.sorted(Comparator.comparing(Camera::getLocationDescription)).collect(Collectors.toList()));
		return "index";
	}

	@GetMapping(path = "/camera/{camera}")
	public String showCameraPage(Model model, @PathVariable String camera) throws IOException {
		model.addAttribute("camera", cameraService.getCamera(camera));
		model.addAttribute("detections", detectionFileService.readDetectionsForToday(camera));
		return "camera";
	}

	@GetMapping(path = "/newDetections/{camera}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Detection> getNewDetections(@PathVariable String camera) {
		Detections detections = detectionAggregatorService.getDetections(camera);
		return (detections != null) ? detections.getDetections() : new ArrayList<Detection>();
	}
}
