package uk.me.ruthmills.motioncorrelator.model.image;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.AlarmState;

public class Image {

	private long sequence;
	private LocalDateTime timestamp;
	private AlarmState alarmState;
	private byte[] bytes;

	public Image(LocalDateTime timestamp, byte[] bytes) {
		this.timestamp = timestamp;
		this.bytes = bytes;
	}

	public Image(LocalDateTime timestamp, byte[] bytes, AlarmState alarmState) {
		this.timestamp = timestamp;
		this.bytes = bytes;
		this.alarmState = alarmState;
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
}
