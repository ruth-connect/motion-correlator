package uk.me.ruthmills.motioncorrelator.model;

public class Video {

	private String videoPath;
	private String filename;

	public Video() {
	}

	public Video(String videoPath, String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return videoPath + "/" + filename;
	}

	public String getTimestamp() {
		int underscoreIndex = filename.indexOf(".");
		return filename.substring(underscoreIndex - 13, underscoreIndex + 5);
	}
}
