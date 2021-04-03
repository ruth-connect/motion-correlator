package uk.me.ruthmills.motioncorrelator.model;

public class Camera {

	private String name;
	private String displayName;
	private String streamUrl;
	private String adminUrl;
	private String location;
	private boolean connected;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("name: " + name);
		stringBuilder.append(", displayName: " + displayName);
		stringBuilder.append(", url: " + streamUrl);
		stringBuilder.append(", adminUrl: " + adminUrl);
		stringBuilder.append(", location: " + location);
		stringBuilder.append(", connected: " + connected + "\n");
		return stringBuilder.toString();
	}
}
