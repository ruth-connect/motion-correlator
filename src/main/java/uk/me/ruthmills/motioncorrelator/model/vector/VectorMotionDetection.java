package uk.me.ruthmills.motioncorrelator.model.vector;

import java.time.LocalDateTime;

public class VectorMotionDetection {

	private LocalDateTime timestamp;
	private Vector frameVector;
	private boolean interpolated;

	public VectorMotionDetection(LocalDateTime timestamp, Vector frameVector) {
		this.timestamp = timestamp;
		this.frameVector = frameVector;
	}

	public VectorMotionDetection(LocalDateTime timestamp, Vector frameVector, boolean interpolated) {
		this.timestamp = timestamp;
		this.frameVector = frameVector;
		this.interpolated = interpolated;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Vector getFrameVector() {
		return frameVector;
	}

	public void setFrameVector(Vector frameVector) {
		this.frameVector = frameVector;
	}

	public boolean isInterpolated() {
		return interpolated;
	}

	public void setInterpolated(boolean interpolated) {
		this.interpolated = interpolated;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (timestamp != null) {
			stringBuilder.append("Timestamp: " + timestamp + "\n");
		}
		if (frameVector != null) {
			stringBuilder.append("Frame Vector: " + frameVector + "\n");
		}
		return stringBuilder.toString();
	}
}
