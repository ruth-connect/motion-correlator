package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.util.List;

import uk.me.ruthmills.motioncorrelator.model.Video;

public interface VideoService {

	public List<Video> getVideos(String camera, String year, String month, String day);

	public String getVideoPath(String camera, String year, String month, String day, String filename)
			throws IOException;
}
