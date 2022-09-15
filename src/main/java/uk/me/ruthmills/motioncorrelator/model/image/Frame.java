package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

public class Frame {
	private static final int AVERAGE_IMAGE_START = 10;

	private Image image;
	private Camera camera;
	private volatile Mat blurredFrame;
	private volatile Mat averageFrame;
	private Frame previousFrame;
	private Frame nextFrame;
	private MotionCorrelation motionCorrelation;

	public Frame(Image image) {
		this.image = image;
	}

	public Frame(Image image, Camera camera, Frame previousFrame) {
		this.image = image;
		this.camera = camera;
		this.previousFrame = previousFrame;
		if (previousFrame != null) {
			previousFrame.nextFrame = this;
		}
	}

	public long getSequence() {
		return image.getSequence();
	}

	public LocalDateTime getTimestamp() {
		return image.getTimestamp();
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Mat getBlurredFrame() {
		if (blurredFrame == null) {
			computeAverageFrames(this);
		}
		return blurredFrame;
	}

	public void setBlurredFrame(Mat blurredFrame) {
		this.blurredFrame = blurredFrame;
	}

	public Mat getAverageFrame() {
		if (averageFrame == null) {
			computeAverageFrames(this);
		}
		return averageFrame;
	}

	public Frame getPreviousFrame() {
		return previousFrame;
	}

	public Frame getNextFrame() {
		return nextFrame;
	}

	public MotionCorrelation getMotionCorrelation() {
		return motionCorrelation;
	}

	public void setMotionCorrelation(MotionCorrelation motionCorrelation) {
		this.motionCorrelation = motionCorrelation;
	}

	public void release() {
		if (blurredFrame != null) {
			blurredFrame.release();
			blurredFrame = null;
		}
		if (averageFrame != null) {
			averageFrame.release();
			averageFrame = null;
		}
		nextFrame.previousFrame = null;
		nextFrame = null;
	}

	private static void computeAverageFrames(Frame currentFrame) {
		synchronized (currentFrame.camera) {
			if (currentFrame.averageFrame == null) {
				Frame initialFrame = currentFrame;
				int count = 0;
				while ((initialFrame.previousFrame != null) && (initialFrame.previousFrame.averageFrame == null)
						&& (count < AVERAGE_IMAGE_START * currentFrame.camera.getFramesPerSecond())) {
					initialFrame = initialFrame.previousFrame;
					count++;
				}
				boolean done = false;
				while (initialFrame.averageFrame == null && !done) {
					Mat previousAverageFrame = null;
					Mat averageFrame = new Mat();
					if (initialFrame.previousFrame != null) {
						previousAverageFrame = initialFrame.previousFrame.averageFrame;
					}
					Mat decoded = ImageUtils.decodeImage(initialFrame.image,
							new PersonDetectionParameters().getImageWidthPixels());
					Mat frame = new Mat();
					decoded.convertTo(frame, CvType.CV_32F);
					decoded.release();
					Mat blurredFrame = new Mat();
					Imgproc.GaussianBlur(frame, blurredFrame, new Size(25, 25), 0d);
					frame.release();
					if (previousAverageFrame == null) {
						blurredFrame.copyTo(averageFrame);
					} else {
						previousAverageFrame.copyTo(averageFrame);
						Imgproc.accumulateWeighted(blurredFrame, averageFrame, 0.1d);
					}
					initialFrame.averageFrame = averageFrame;
					initialFrame.blurredFrame = blurredFrame;
					if (initialFrame == currentFrame) {
						done = true;
					} else {
						initialFrame = initialFrame.nextFrame;
					}
				}
			}
		}
	}
}
