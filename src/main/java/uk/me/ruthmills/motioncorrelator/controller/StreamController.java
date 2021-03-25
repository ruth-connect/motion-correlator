package uk.me.ruthmills.motioncorrelator.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import uk.me.ruthmills.motioncorrelator.service.FrameService;

@Controller
@RequestMapping("stream")
public class StreamController {

	@Autowired
	private FrameService frameService;

	private ExecutorService executor = Executors.newCachedThreadPool();

	@GetMapping(value = "/{camera}")
	public ResponseEntity<ResponseBodyEmitter> stream(@PathVariable String camera) {
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();
		executor.execute(() -> {
			try {
				while (true) {
					byte[] image = frameService.getLatestFrame(camera).getImage().getBytes();
					emitter.send(image, MediaType.IMAGE_JPEG);
					Thread.sleep(200);
				}
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return new ResponseEntity<>(emitter, HttpStatus.OK);
	}
}
