package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Video;
import uk.me.ruthmills.motioncorrelator.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

	@Value("${filesystem.media.path}")
	private String mediaPath;

	@Override
	public List<Video> getVideos(String camera, String year, String month, String day) {
		List<Video> videos = new ArrayList<>();
		File directory = new File(mediaPath + "/videos/" + camera + "/" + year + "/" + month + "/" + day);
		if (directory.exists()) {
			List<File> files = Arrays.asList(directory.listFiles());
			videos = files.stream().filter(file -> !file.isDirectory() && file.getName().indexOf("_") > 0)
					.map(file -> new Video("/videos/" + camera + "/" + year + "/" + month + "/" + day, file.getName()))
					.sorted(Comparator.comparing(Video::getTimestamp)).collect(Collectors.toList());
		}
		return videos;
	}

	@Override
	public byte[] getVideo(String camera, String year, String month, String day, String filename) throws IOException {
		String videoPath = mediaPath + "/videos/" + camera + "/" + year + "/" + month + "/" + day;
		return Files.readAllBytes(FileSystems.getDefault().getPath(videoPath, filename));
	}
}
