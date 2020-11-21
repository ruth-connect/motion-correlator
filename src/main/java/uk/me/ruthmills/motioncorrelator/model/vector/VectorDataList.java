package uk.me.ruthmills.motioncorrelator.model.vector;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class VectorDataList extends ArrayList<VectorData> {

	private static final long serialVersionUID = 1L;

	private LocalDateTime timestamp;

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
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
		stringBuilder.append("Timestamp: " + timestamp + "\n");
		for (VectorData vectorData : this) {
			stringBuilder.append(vectorData + "\n");
		}
		return stringBuilder.toString();
	}
}
