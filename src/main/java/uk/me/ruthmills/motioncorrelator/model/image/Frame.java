package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

import org.opencv.core.Mat;

public class Frame {

	private Image image;
	private Mat blurredFrame;
	private Mat averageFrame;
	private Frame previousFrame;

	public Frame(Image image, Mat blurredFrame, Mat averageFrame, Frame previousFrame) {
		this.image = image;
		this.blurredFrame = blurredFrame;
		this.averageFrame = averageFrame;
		this.previousFrame = previousFrame;
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
		return blurredFrame;
	}

	public void setBlurredFrame(Mat blurredFrame) {
		this.blurredFrame = blurredFrame;
	}

	public Mat getAverageFrame() {
		return averageFrame;
	}

	public void setAverageFrame(Mat averageFrame) {
		this.averageFrame = averageFrame;
	}

	public Frame getPreviousFrame() {
		return previousFrame;
	}

	public void setPreviousFrame(Frame previousFrame) {
		this.previousFrame = previousFrame;
	}
}