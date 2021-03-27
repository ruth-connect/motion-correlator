package uk.me.ruthmills.motioncorrelator.model;

public class Camera {

	private String name;
	private String streamUrl;
	private String adminUrl;
	private String location;
	private String locationDescription;
	private boolean connected;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String url) {
		this.streamUrl = url;
	}

	public String getAdminUrl() {
		return adminUrl;
	}

	public void setAdminUrl(String adminUrl) {
		this.adminUrl = adminUrl;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("name: " + name);
		stringBuilder.append(", url: " + streamUrl);
		stringBuilder.append(", location: " + location);
		stringBuilder.append(", locationDescription: " + locationDescription);
		stringBuilder.append(", connected: " + connected + "\n");
		return stringBuilder.toString();
	}
}
