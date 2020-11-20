package uk.me.ruthmills.motioncorrelator.model.vector;

import java.time.LocalDateTime;

public class Timestamp extends VectorData {

	private LocalDateTime timestamp;

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
