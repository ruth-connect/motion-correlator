package uk.me.ruthmills.motioncorrelator.model.vector;

import java.time.LocalDateTime;
import java.util.List;

public class VectorMotionDetection {

	private LocalDateTime timestamp;
	private Vector frameVector;
	private List<Vector> regionVectors;
	private Burst burst;
	private ExternalTrigger externalTrigger;
	private boolean interpolated;

	public VectorMotionDetection() {
	}

	public VectorMotionDetection(LocalDateTime timestamp, Vector frameVector, boolean interpolated) {
		this.timestamp = timestamp;
		this.frameVector = frameVector;
		this.interpolated = interpolated;
	}

	public VectorMotionDetection(LocalDateTime timestamp, Vector frameVector, List<Vector> regionVectors, Burst burst,
			ExternalTrigger externalTrigger) {
		this.timestamp = timestamp;
		this.frameVector = frameVector;
		this.regionVectors = regionVectors;
		this.burst = burst;
		this.externalTrigger = externalTrigger;
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

	public List<Vector> getRegionVectors() {
		return regionVectors;
	}

	public void setRegionVectors(List<Vector> regionVectors) {
		this.regionVectors = regionVectors;
	}

	public Burst getBurst() {
		return burst;
	}

	public void setBurst(Burst burst) {
		this.burst = burst;
	}

	public ExternalTrigger getExternalTrigger() {
		return externalTrigger;
	}

	public void setExternalTrigger(ExternalTrigger externalTrigger) {
		this.externalTrigger = externalTrigger;
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
		if (regionVectors != null) {
			stringBuilder.append("Region Vectors: " + regionVectors + "\n");
		}
		if (burst != null) {
			stringBuilder.append("Burst: " + burst + "\n");
		}
		if (externalTrigger != null) {
			stringBuilder.append("External Trigger: " + externalTrigger + "\n");
		}
		return stringBuilder.toString();
	}
}
