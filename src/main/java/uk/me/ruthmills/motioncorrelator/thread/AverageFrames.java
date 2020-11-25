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
import uk.me.ruthmills.motioncorrelator.model.image.AverageFrame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

public class AverageFrames implements Runnable {

	private static final int MAX_QUEUE_SIZE = 100;

	private Camera camera;
	private BlockingQueue<Image> unprocessedImages = new LinkedBlockingDeque<>();
	private Deque<AverageFrame> averageFrames = new ConcurrentLinkedDeque<>();
	private int size;
	private Thread averageFrameProcessor;

	private static final Logger logger = LoggerFactory.getLogger(AverageFrames.class);

	public AverageFrames(Camera camera) {
		this.camera = camera;
	}

	public void initialise() {
		this.averageFrameProcessor = new Thread(this, camera.getName() + " average frame processor");
		averageFrameProcessor.start();
		logger.info("Started average frame processor thread for camera: " + camera.getName());
	}

	public void addCurrentFrame(Image image) {
		unprocessedImages.offer(image);
	}

	public Deque<AverageFrame> getAverageFrames() {
		return averageFrames;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Mat averageFrameOld = null;
				Mat averageFrameNew = new Mat();
				Image image = unprocessedImages.take();
				if (size > 0) {
					averageFrameOld = averageFrames.getLast().getMat();
				}
				Mat decoded = ImageUtils.decodeImage(image, new PersonDetectionParameters().getImageWidthPixels());
				Mat frame = new Mat();
				decoded.convertTo(frame, CvType.CV_32F);
				decoded.release();
				Mat blurredFrame = new Mat();
				Imgproc.GaussianBlur(frame, blurredFrame, new Size(25, 25), 0d);
				frame.release();
				if (averageFrameOld == null) {
					averageFrameNew = blurredFrame;
				} else {
					averageFrameOld.copyTo(averageFrameNew);
					Imgproc.accumulateWeighted(blurredFrame, averageFrameNew, 0.1d);
					blurredFrame.release();
				}
				averageFrames.addLast(new AverageFrame(image.getTimestamp(), averageFrameNew));
				if (size > MAX_QUEUE_SIZE) {
					logger.info("Removing oldest average frame from queue for camera: " + camera.getName());
					Mat expiredAverageFrame = averageFrames.removeFirst().getMat();
					expiredAverageFrame.release();
				} else {
					size++;
				}
			} catch (Exception ex) {
				logger.error("Failed to process average image for camera: " + camera.getName(), ex);
			}
		}
	}
}
