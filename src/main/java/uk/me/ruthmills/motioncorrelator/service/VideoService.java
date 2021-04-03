package uk.me.ruthmills.motioncorrelator.service;

import java.util.Map;

public interface VideoService {

	public Map<String, String> getVideos(String camera, String year, String month, String day);
}
