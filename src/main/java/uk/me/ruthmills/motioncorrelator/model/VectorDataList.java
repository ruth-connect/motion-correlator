package uk.me.ruthmills.motioncorrelator.model;

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
}
