package uk.me.ruthmills.motioncorrelator.mjpeg;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MjpegStream implements Runnable {

	private static final int INPUT_BUFFER_SIZE = 16384;
	private static final int MAX_QUEUE_SIZE = 100;

	@Autowired
	private HomeAssistantService homeAssistantService;

	private Camera camera;
	private URLConnection conn;
	private BufferedInputStream inputStream;
	private ByteArrayOutputStream outputStream;
	private boolean connected;
	protected byte[] currentFrame = new byte[0];
	private Deque<Image> images = new ConcurrentLinkedDeque<>();
	private int size;
	private Thread streamReader;

	private static final Logger logger = LoggerFactory.getLogger(MjpegStream.class);

	public MjpegStream(Camera camera) {
		this.camera = camera;
	}

	public void initialise() {
		this.streamReader = new Thread(this, camera.getName() + " stream reader");
		streamReader.start();
	}

	public Deque<Image> getImages() {
		return images;
	}

	public void run() {
		while (true) {
			connect();
			if (connected) {
				int prev = 0;
				int cur = 0;

				try {
					// EOF is -1
					while (connected && (inputStream != null) && ((cur = inputStream.read()) >= 0)) {
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
								read();
							}
						}
						prev = cur;
					}
				} catch (Exception e) {
					logger.error("Exception", e);
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
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Exception ex) {
						// do nothing
					} finally {
						inputStream = null;
					}
				}
			}
		}
	}

	private BufferedInputStream openConnection() {
		BufferedInputStream bufferedInputStream = null;
		try {
			logger.info("Connecting to: " + camera.getUrl());
			URL url = new URL(camera.getUrl());
			conn = url.openConnection();
			conn.setReadTimeout(5000); // 5 seconds
			conn.connect();
			bufferedInputStream = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
			logger.info("Connected to: " + camera.getUrl() + " successfully!");
			homeAssistantService.notifyCameraConnected(camera);
		} catch (MalformedURLException ex) {
			logger.error("Invalid URL", ex);
		} catch (IOException ex) {
			logger.error("Unable to connect: ", ex);
		}
		return bufferedInputStream;
	}

	public void connect() {
		if (inputStream == null) {
			inputStream = openConnection();
			connected = true;
		}
	}

	private void read() {
		Image image = new Image();
		image.setTimestamp(LocalDateTime.now());
		image.setBytes(currentFrame);
		images.addLast(image);
		if (size >= MAX_QUEUE_SIZE) {
			images.removeFirst();
		} else {
			size++;
		}
	}
}
