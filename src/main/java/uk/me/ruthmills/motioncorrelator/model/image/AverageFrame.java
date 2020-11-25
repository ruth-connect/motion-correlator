package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

import org.opencv.core.Mat;

public class AverageFrame {

	private LocalDateTime timestamp;
	private Mat mat;

	public AverageFrame(LocalDateTime timestamp, Mat averageFrame) {
		this.timestamp = timestamp;
		this.mat = averageFrame;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}
}
