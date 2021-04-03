package uk.me.ruthmills.motioncorrelator.service;

import java.util.List;

import uk.me.ruthmills.motioncorrelator.model.Video;

public interface VideoService {

	public List<Video> getVideos(String camera, String year, String month, String day);
}
