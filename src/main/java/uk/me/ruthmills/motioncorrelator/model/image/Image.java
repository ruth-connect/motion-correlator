package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

public class Image {

	private long sequence;
	private LocalDateTime timestamp;
	private byte[] bytes;

	public Image(LocalDateTime timestamp, byte[] bytes) {
		this.timestamp = timestamp;
		this.bytes = bytes;
	}

	public Image(long sequence, LocalDateTime timestamp, byte[] bytes) {
		this.sequence = sequence;
		this.timestamp = timestamp;
		this.bytes = bytes;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public long getSequence() {
		return sequence;
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
