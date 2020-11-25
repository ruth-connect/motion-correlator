package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

public class Image {

	private LocalDateTime timestamp;
	private byte[] bytes;

	public Image(LocalDateTime timestamp, byte[] bytes) {
		this.timestamp = timestamp;
		this.bytes = bytes;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
