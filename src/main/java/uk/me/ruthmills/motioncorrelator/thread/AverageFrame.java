package uk.me.ruthmills.motioncorrelator.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

public class AverageFrame implements Runnable {

	private Camera camera;
	private Mat averageFrame;
	private BlockingQueue<Image> unprocessedImageQueue = new LinkedBlockingDeque<>();
	private Thread averageFrameProcessor;

	private static final Logger logger = LoggerFactory.getLogger(AverageFrame.class);

	public AverageFrame(Camera camera) {
		this.camera = camera;
	}

	public void initialise() {
		this.averageFrameProcessor = new Thread(this, camera.getName() + " average frame processor");
		averageFrameProcessor.start();
		logger.info("Started average frame processor thread for camera: " + camera.getName());
	}

	public void addCurrentFrame(Image image) {
		unprocessedImageQueue.offer(image);
	}

	public Image getAverageFrame() {
		if (averageFrame == null) {
			return null;
		}
		synchronized (averageFrame) {
			return ImageUtils.encodeImage(averageFrame);
		}
	}

	public Mat getAverageFrameMat() {
		synchronized (averageFrame) {
			Mat averageFrameCopy = new Mat();
			averageFrame.copyTo(averageFrameCopy);
			return averageFrameCopy;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Image image = unprocessedImageQueue.take();
				logger.info("Queue size for camera: " + camera + ": " + unprocessedImageQueue.size());
				Mat decoded = ImageUtils.decodeImage(image, new PersonDetectionParameters().getImageWidthPixels());
				Mat frame = new Mat();
				decoded.convertTo(frame, CvType.CV_32F);
				decoded.release();
				Mat blurredFrame = new Mat();
				Imgproc.GaussianBlur(frame, blurredFrame, new Size(25, 25), 0d);
				frame.release();
				if (averageFrame == null) {
					averageFrame = blurredFrame;
				} else {
					Imgproc.accumulateWeighted(blurredFrame, averageFrame, 0.1d);
					blurredFrame.release();
				}
			} catch (Exception ex) {
				logger.error("Failed to process average image", ex);
			}
		}
	}
}
