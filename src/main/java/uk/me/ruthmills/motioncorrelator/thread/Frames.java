package uk.me.ruthmills.motioncorrelator.thread;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

public class Frames implements Runnable {

	private static final int MAX_QUEUE_SIZE = 100; // 16.7 seconds at 6 frames per second.

	private Camera camera;
	private BlockingQueue<Image> unprocessedImages = new LinkedBlockingDeque<>(1); // 1 frame only.
	private Deque<Frame> frames = new ConcurrentLinkedDeque<>();
	private int size;
	private Thread frameProcessor;

	private static final Logger logger = LoggerFactory.getLogger(Frames.class);

	public Frames(Camera camera) {
		this.camera = camera;
	}

	public void initialise() {
		this.frameProcessor = new Thread(this, camera.getName() + " frame processor");
		frameProcessor.start();
		logger.info("Started frame processor thread for camera: " + camera.getName());
	}

	public void addCurrentFrame(Image image) {
		if (!unprocessedImages.offer(image)) {
			logger.info("Frame dropped. Camera: " + camera.getName() + ", Timestamp: " + image.getTimestamp());
		}
	}

	public Deque<Frame> getFrames() {
		return frames;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Image image = unprocessedImages.take();
				Frame previousFrame = null;
				Mat previousAverageFrame = null;
				Mat currentAverageFrame = new Mat();
				if (size > 0) {
					previousFrame = frames.getLast();
					previousAverageFrame = previousFrame.getAverageFrame();
				}
				Mat decoded = ImageUtils.decodeImage(image, new PersonDetectionParameters().getImageWidthPixels());
				Mat frame = new Mat();
				decoded.convertTo(frame, CvType.CV_32F);
				decoded.release();
				Mat blurredFrame = new Mat();
				Imgproc.GaussianBlur(frame, blurredFrame, new Size(25, 25), 0d);
				frame.release();
				if (previousAverageFrame == null) {
					blurredFrame.copyTo(currentAverageFrame);
				} else {
					previousAverageFrame.copyTo(currentAverageFrame);
					Imgproc.accumulateWeighted(blurredFrame, currentAverageFrame, 0.1d);
				}
				Frame newFrame = new Frame(image, blurredFrame, currentAverageFrame, previousFrame);
				if (size > 0) {
					frames.getLast().setNextFrame(newFrame);
				}
				frames.addLast(newFrame);

				if (size > MAX_QUEUE_SIZE) {
					Frame expiredFrame = frames.removeFirst();
					expiredFrame.getBlurredFrame().release();
					expiredFrame.getAverageFrame().release();
					expiredFrame.setNextFrame(null);
					frames.getFirst().setPreviousFrame(null);
				} else {
					size++;
				}
			} catch (Exception ex) {
				logger.error("Failed to process average image for camera: " + camera.getName(), ex);
			}
		}
	}
}
