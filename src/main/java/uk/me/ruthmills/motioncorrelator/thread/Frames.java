package uk.me.ruthmills.motioncorrelator.thread;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;

public class Frames implements Runnable {

	private static final int MAX_QUEUE_SIZE = 60; // 1 minute.

	private Camera camera;
	private BlockingQueue<Image> unprocessedImages = new LinkedBlockingDeque<>(1); // 1 frame only.
	private Deque<Frame> frames = new ConcurrentLinkedDeque<>();
	private int size;
	private Thread frameProcessor;
	private long sequence;

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
			logger.warn("Frame dropped. Camera: " + camera.getName() + ", Timestamp: " + image.getTimestamp());
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
				image.setSequence(sequence++);
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
			} catch (Exception ex) {
				logger.error("Failed to process average image for camera: " + camera.getName(), ex);
			} catch (UnsatisfiedLinkError ex) {
				logger.error("Unsatisfied link error for Frame processor thread for camera: " + camera.getName()
						+ " - should only ever happen on start up!", ex);
			}
		}
	}
}
