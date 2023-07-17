package uk.me.ruthmills.motioncorrelator.thread;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.AlarmStateService;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MjpegStream implements Runnable {

	private static final int INPUT_BUFFER_SIZE = 16384;
	private static final int MAX_QUEUE_SIZE = 60; // 1 minute.

	@Autowired
	private HomeAssistantService homeAssistantService;

	@Autowired
	private AlarmStateService alarmStateService;

	private Camera camera;
	private URLConnection conn;
	private ByteArrayOutputStream outputStream;
	private byte[] currentFrame = new byte[0];
	private Thread streamReader;
	private long sequence;
	private int size;
	private long startTimeMillis;
	private Deque<Frame> frames = new ConcurrentLinkedDeque<>();

	private static final Logger logger = LoggerFactory.getLogger(MjpegStream.class);

	public MjpegStream(Camera camera) {
		this.camera = camera;
	}

	public void initialise() {
		this.streamReader = new Thread(this, camera.getName() + " stream reader");
		streamReader.setPriority(6); // higher priority than normal (5).
		streamReader.start();
		logger.info("Started MJPEG stream reader thread for camera: " + camera.getName());
	}

	public Deque<Frame> getFrames() {
		return frames;
	}

	public void run() {
		while (true) {
			try (InputStream inputStream = openConnection()) {
				int prev = 0;
				int cur = 0;
				sequence = 0;

				// EOF is -1
				while ((inputStream != null) && ((cur = inputStream.read()) >= 0)) {
					if (prev == 0xFF && cur == 0xD8) {
						LocalDateTime now = LocalDateTime.now();
						this.startTimeMillis = TimeUtils.toMilliseconds(now);
						outputStream = new ByteArrayOutputStream(INPUT_BUFFER_SIZE);
						outputStream.write((byte) prev);
					}
					if (outputStream != null) {
						outputStream.write((byte) cur);
						if (prev == 0xFF && cur == 0xD9) {
							outputStream.flush();
							currentFrame = outputStream.toByteArray();
							outputStream.close();
							// the image is now available - read it
							handleNewFrame();
							if (!camera.isConnected()) {
								logger.info("Connected to: " + camera.getStreamUrl() + " successfully!");
								homeAssistantService.notifyCameraConnected(camera);
								camera.setConnected(true);
							}
						}
					}
					prev = cur;
				}
			} catch (Exception ex) {
				if (camera.isConnected()) {
					logger.error("Failed to read stream", ex);
				}
			}

			if (camera.isConnected()) {
				homeAssistantService.notifyCameraConnectionFailed(camera);
				camera.setConnected(false);
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				logger.error("Interrupted Exception", e);
			}
		}
	}

	private BufferedInputStream openConnection() throws IOException {
		BufferedInputStream bufferedInputStream = null;
		URL url = new URL(camera.getStreamUrl());
		conn = url.openConnection();
		conn.setReadTimeout(5000); // 5 seconds
		conn.connect();
		bufferedInputStream = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
		return bufferedInputStream;
	}

	private void handleNewFrame() throws ImageReadException, IOException {
		sequence++;
		LocalDateTime now = LocalDateTime.now();
		long millisNow = TimeUtils.toMilliseconds(now);

		JpegImageMetadata imageMetadata = (JpegImageMetadata) Imaging.getMetadata(currentFrame);
		long imageTimestampMillis = 0;
		int width = 640; // default
		int height = 480; // default
		try {
			imageTimestampMillis = Long
					.parseLong(imageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_OWNER_NAME)
							.getValueDescription().replaceAll("\'", ""));
			width = imageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH).getIntValue();
			height = imageMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH)
					.getIntValue();
		} catch (Exception ex) {
			imageTimestampMillis = this.startTimeMillis;
		}

		long latency = millisNow - imageTimestampMillis;
		if (latency < 0) {
			latency = 0;
		}

		// Process the latency.
		camera.getLatency().processLatency((int) latency);

		// Do not allow the latency to get more than 5 seconds behind.
		if (latency > 5000) {
			homeAssistantService.notifyCameraStreamBehindSchedule(camera);
			throw new RuntimeException(
					camera.getLocation() + " camera stream is: " + (latency) + " milliseconds behind schedule");
		}

		Image image = new Image(TimeUtils.fromMilliseconds(imageTimestampMillis), currentFrame,
				alarmStateService.getAlarmState(), sequence, (int) latency, width, height);
		Frame previousFrame = null;
		if (size > 0) {
			previousFrame = frames.getLast();
		}
		frames.addLast(new Frame(image, camera, previousFrame));

		if (size > MAX_QUEUE_SIZE * camera.getFramesPerSecond()) {
			frames.removeFirst().release();
		} else {
			size++;
		}
	}
}
