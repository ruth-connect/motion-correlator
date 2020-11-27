package uk.me.ruthmills.motioncorrelator.model.vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
				if (vector.getRegion().equals("f")) {
					return vector;
				}
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
