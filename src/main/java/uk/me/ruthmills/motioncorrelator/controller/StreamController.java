package uk.me.ruthmills.motioncorrelator.controller;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.service.FrameService;

@Controller
@RequestMapping("stream")
public class StreamController {

	@Autowired
	private FrameService frameService;

	private static final Logger logger = LoggerFactory.getLogger(StreamController.class);

	@GetMapping(value = "/{camera}", produces = "multipart/x-mixed-replace; boundary=--BoundaryString")
	public StreamingResponseBody stream(@PathVariable String camera) {
		return new StreamingResponseBody() {

			@Override
			public void writeTo(OutputStream out) throws IOException {
				long oldSequence = 0;
				while (true) {
					try {
						Frame frame = frameService.getLatestFrame(camera);
						long currentSequence = frame.getSequence();
						if (currentSequence != oldSequence) {
							logger.info("About to write image with sequence: " + currentSequence);
							byte[] image = frame.getImage().getBytes();
							out.write(("--BoundaryString\r\n" + "Content-type: image/jpeg\r\n" + "Content-Length: "
									+ image.length + "\r\n\r\n").getBytes());
							out.write(image);
							out.write("\r\n\r\n".getBytes());
							out.flush();
							logger.info("Wrote image with sequence: " + currentSequence);
							oldSequence = currentSequence;
						} else {
							Thread.sleep(20);
						}
					} catch (InterruptedException ex) {
					}
				}
			}
		};
	}
}
