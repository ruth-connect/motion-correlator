package uk.me.ruthmills.motioncorrelator.thread;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MjpegStream implements Runnable {

	private static final int INPUT_BUFFER_SIZE = 16384;

	@Autowired
	private HomeAssistantService homeAssistantService;

	@Autowired
	private FrameService frameService;

	private Camera camera;
	private URLConnection conn;
	private ByteArrayOutputStream outputStream;
	private boolean connected;
	protected byte[] currentFrame = new byte[0];
	private Thread streamReader;

	private static final Logger logger = LoggerFactory.getLogger(MjpegStream.class);

	public MjpegStream(Camera camera) {
		this.camera = camera;
	}

	public void initialise() {
		this.streamReader = new Thread(this, camera.getName() + " stream reader");
		streamReader.start();
	}

	public void run() {
		while (true) {
			try (InputStream inputStream = openConnection()) {
				int prev = 0;
				int cur = 0;

				// EOF is -1
				while ((inputStream != null) && ((cur = inputStream.read()) >= 0)) {
					if (prev == 0xFF && cur == 0xD8) {
						outputStream = new ByteArrayOutputStream(INPUT_BUFFER_SIZE);
						outputStream.write((byte) prev);
					}
					if (outputStream != null) {
						outputStream.write((byte) cur);
						if (prev == 0xFF && cur == 0xD9) {
							synchronized (currentFrame) {
								currentFrame = outputStream.toByteArray();
							}
							outputStream.close();
							// the image is now available - read it
							handleNewFrame();
							if (connected == false) {
								logger.info("Connected to: " + camera.getUrl() + " successfully!");
								homeAssistantService.notifyCameraConnected(camera);
								connected = true;
							}
						}
					}
					prev = cur;
				}
			} catch (Exception ex) {
				logger.error("Failed to read stream", ex);
			}

			if (connected) {
				homeAssistantService.notifyCameraConnectionFailed(camera);
				connected = false;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Interrupted Exception", e);
			}
		}
	}

	private BufferedInputStream openConnection() throws IOException {
		BufferedInputStream bufferedInputStream = null;
		logger.info("Opening connection to: " + camera.getUrl());
		URL url = new URL(camera.getUrl());
		conn = url.openConnection();
		conn.setReadTimeout(5000); // 5 seconds
		conn.connect();
		bufferedInputStream = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
		return bufferedInputStream;
	}

	private void handleNewFrame() {
		Image image = new Image(LocalDateTime.now(), currentFrame);
		frameService.addCurrentFrame(camera.getName(), image);
	}
}