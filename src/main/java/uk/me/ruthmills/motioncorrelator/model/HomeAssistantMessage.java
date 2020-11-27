package uk.me.ruthmills.motioncorrelator.model;

public class HomeAssistantMessage {

	private String sensorName;
	private String sensorValue;

	public HomeAssistantMessage(String sensorName, String sensorValue) {
		this.sensorName = sensorName;
		this.sensorValue = sensorValue;
	}

	public String getSensorName() {
		return sensorName;
	}

	public String getSensorValue() {
		return sensorValue;
	}
}
