package uk.me.ruthmills.motioncorrelator.model;

public class Camera {

	private String name;
	private String url;
	private String location;
	private String locationDescription;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("name: " + name);
		stringBuilder.append(", url: " + url);
		stringBuilder.append(", location: " + location);
		stringBuilder.append(", locationDescription: " + locationDescription);
		return stringBuilder.toString();
	}
}
