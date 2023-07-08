package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.AlarmState;

public class Image {

	private long sequence;
	private LocalDateTime timestamp;
	private AlarmState alarmState;
	private byte[] bytes;
	private int latency;
	private int width;
	private int height;

	public Image(LocalDateTime timestamp, byte[] bytes) {
		this.timestamp = timestamp;
		this.bytes = bytes;
	}

	public Image(LocalDateTime timestamp, byte[] bytes, AlarmState alarmState, long sequence, int latency, int width,
			int height) {
		this.timestamp = timestamp;
		this.bytes = bytes;
		this.alarmState = alarmState;
		this.sequence = sequence;
		this.latency = latency;
		this.width = width;
		this.height = height;
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

	public AlarmState getAlarmState() {
		return alarmState;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public int getLatency() {
		return latency;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
