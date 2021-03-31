package uk.me.ruthmills.motioncorrelator.model.vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VectorDataList extends ArrayList<VectorData> {

	private static final long serialVersionUID = 1L;

	private String camera;
	private LocalDateTime timestamp;

	public VectorDataList(String camera, LocalDateTime timestamp, List<VectorData> vectorDataList) {
		this.camera = camera;
		this.timestamp = timestamp;
		this.addAll(vectorDataList);
	}

	public String getCamera() {
		return camera;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public Vector getFrameVector() {
		for (VectorData vectorData : this) {
			if (vectorData instanceof Vector) {
				Vector vector = (Vector) vectorData;
				if (vector.getRegion().equals("f") && !vector.isZeroVector()) {
					return vector;
				}
			}
		}
		return null;
	}

	public List<Vector> getRegionVectors() {
		List<Vector> regionVectors = new ArrayList<>();
		for (VectorData vectorData : this) {
			if (vectorData instanceof Vector) {
				Vector vector = (Vector) vectorData;
				if (!vector.getRegion().equals("f")) {
					regionVectors.add(vector);
				}
			}
		}
		return regionVectors.stream().sorted(Comparator.comparing(Vector::getRegion)).collect(Collectors.toList());
	}

	public Burst getBurst() {
		for (VectorData vectorData : this) {
			if (vectorData instanceof Burst) {
				return (Burst) vectorData;
			}
		}
		return null;
	}

	public ExternalTrigger getExternalTrigger() {
		for (VectorData vectorData : this) {
			if (vectorData instanceof ExternalTrigger) {
				return (ExternalTrigger) vectorData;
			}
		}
		return null;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Camera: " + camera + "\n");
		stringBuilder.append("Timestamp: " + timestamp + "\n");
		for (VectorData vectorData : this) {
			stringBuilder.append(vectorData + "\n");
		}
		return stringBuilder.toString();
	}
}
