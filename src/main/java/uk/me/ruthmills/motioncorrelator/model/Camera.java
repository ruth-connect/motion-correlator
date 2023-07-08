package uk.me.ruthmills.motioncorrelator.model;

public class Camera {

	/**
	 * Name of the camera.
	 */
	private String name;

	/**
	 * Display Name of the camera (as shown in the UI).
	 */
	private String displayName;

	/**
	 * URL of the camera's MJPEG stream.
	 */
	private String streamUrl;

	/**
	 * Admin URL of the camera.
	 */
	private String adminUrl;

	/**
	 * Location of the camera (as shown in the UI).
	 */
	private String location;

	/**
	 * Number of frames per second of the MJPEG stream for this camera.
	 */
	private int framesPerSecond;

	/**
	 * Connected flag. true = connected, false = disconnected.
	 */
	private boolean connected;

	/**
	 * Latency.
	 */
	private Latency latency;

	/**
	 * Get the name of the camera.
	 * 
	 * @return The name of the camera.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the camera.
	 * 
	 * @param name The name of the camera.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the display name of the camera (as shown in the UI).
	 * 
	 * @return The display name of the camera (as shown in the UI).
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display name of the camera (as shown in the UI).
	 * 
	 * @param displayName The display name of the camera (as shown in the UI).
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the URL of the camera's MJPEG stream.
	 * 
	 * @return The URL of the camera's MJPEG stream.
	 */
	public String getStreamUrl() {
		return streamUrl;
	}

	/**
	 * Set the URL of the camera's MJPEG stream.
	 * 
	 * @param url The URL of the camera's MJPEG stream.
	 */
	public void setStreamUrl(String url) {
		this.streamUrl = url;
	}

	/**
	 * Get the admin URL of the camera.
	 * 
	 * @return The admin URL of the camera.
	 */
	public String getAdminUrl() {
		return adminUrl;
	}

	/**
	 * Set the admin URL of the camera.
	 * 
	 * @param adminUrl The admin URL of the camera.
	 */
	public void setAdminUrl(String adminUrl) {
		this.adminUrl = adminUrl;
	}

	/**
	 * Get the location of the camera (as shown in the UI).
	 * 
	 * @return The location of the camera (as shown in the UI).
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Set the location of the camera (as shown in the UI).
	 * 
	 * @param location The location of the camera (as shown in the UI).
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Get the number of frames per second of the MJPEG stream for this camera.
	 * 
	 * @return The number of frames per second of the MJPEG stream for this camera.
	 */
	public int getFramesPerSecond() {
		return framesPerSecond;
	}

	/**
	 * Set the number of frames per second of the MJPEG stream for this camera.
	 *
	 * @param framesPerSecond The number of frames per second of the MJPEG stream
	 *                        for this camera.
	 */
	public void setFramesPerSecond(int framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
	}

	/**
	 * Get the connected flag.
	 * 
	 * @return true if the camera is connected, false if the camera is disconnected.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Set the connected flag.
	 * 
	 * @param connected true if the camera is connected, false if the camera is
	 *                  disconnected.
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Get the latency.
	 */
	public synchronized Latency getLatency() {
		if (latency == null) {
			latency = new Latency();
		}
		return latency;
	}

	/**
	 * Clear the latency.
	 */
	public synchronized void clearLatency() {
		latency = null;
	}

	/**
	 * Convert to a String.
	 * 
	 * @return String representation of this object.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("name: " + name);
		stringBuilder.append(", displayName: " + displayName);
		stringBuilder.append(", url: " + streamUrl);
		stringBuilder.append(", adminUrl: " + adminUrl);
		stringBuilder.append(", location: " + location);
		stringBuilder.append(", framesPerSecond: " + framesPerSecond);
		stringBuilder.append(", connected: " + connected + "\n");
		return stringBuilder.toString();
	}
}
