package uk.me.ruthmills.motioncorrelator.model;

public class HomeAssistantMessage {

	private String location;
	private String type;

	public HomeAssistantMessage(String location, String type) {
		this.location = location;
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public String getType() {
		return type;
	}
}
