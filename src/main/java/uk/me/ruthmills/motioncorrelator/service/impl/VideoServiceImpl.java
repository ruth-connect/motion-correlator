package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Video;
import uk.me.ruthmills.motioncorrelator.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

	@Value("${filesystem.media.path}")
	private String mediaPath;

	@Value("${videoUrlPrefix}")
	private String videoUrlPrefix;

	@Override
	public Map<String, String> getVideos(String camera, String year, String month, String day) {
		Map<String, String> videoMap = new HashMap<>();
		File directory = new File(mediaPath + "/videos/" + camera + "/" + year + "/" + month + "/" + day);
		if (directory.exists()) {
			List<File> files = Arrays.asList(directory.listFiles());
			List<Video> videos = files.stream().filter(file -> !file.isDirectory() && file.getName().indexOf("_") > 0)
					.map(file -> new Video(videoUrlPrefix + "/videos/" + camera + "/" + year + "/" + month + "/" + day,
							file.getName()))
					.collect(Collectors.toList());
			for (Video video : videos) {
				videoMap.put(video.getTimestamp(), video.getPath());
			}
		}
		return videoMap;
	}
}
