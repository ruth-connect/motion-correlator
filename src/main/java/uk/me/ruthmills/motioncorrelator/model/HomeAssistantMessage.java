package uk.me.ruthmills.motioncorrelator.model;

/**
 * Message to send to Home Assistant when something happens.
 * 
 * @author ruth
 */
public class HomeAssistantMessage {

	/**
	 * Name of the Home Assistant sensor.
	 */
	private String sensorName;

	/**
	 * Value to set the sensor to.
	 */
	private String sensorValue;

	/**
	 * Constructor.
	 * 
	 * @param sensorName  The name of the Home Assistant sensor.
	 * @param sensorValue The value to set the sensor to.
	 */
	public HomeAssistantMessage(String sensorName, String sensorValue) {
		this.sensorName = sensorName;
		this.sensorValue = sensorValue;
	}

	/**
	 * Get the name of the Home Assistant sensor.
	 * 
	 * @return The name of the Home Assistant sensor.
	 */
	public String getSensorName() {
		return sensorName;
	}

	/**
	 * Get the value to set the sensor to.
	 * 
	 * @return The value to set the sensor to.
	 */
	public String getSensorValue() {
		return sensorValue;
	}
}
