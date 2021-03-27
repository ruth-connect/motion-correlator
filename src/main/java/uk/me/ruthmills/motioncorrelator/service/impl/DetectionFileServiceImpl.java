package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.service.DetectionFileService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class DetectionFileServiceImpl implements DetectionFileService {

	private static final String DETECTION_PATH_PREFIX = "/mnt/media/detections/";

	@Override
	public void writeDetection(Detection detection) throws IOException, JsonMappingException, JsonGenerationException {
		String detectionFilename = getDetectionPath(detection.getCamera(), detection.getTimestamp()) + "/"
				+ detection.getTimestamp() + "-" + detection.getSequence() + ".json";
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(detectionFilename), detection);
	}

	@Override
	public List<Detection> readDetections(String camera, String year, String month, String day, String hour)
			throws IOException {
		String detectionPath = DETECTION_PATH_PREFIX + camera + "/" + year + "/" + month + "/" + day + "/" + hour;
		ObjectMapper objectMapper = new ObjectMapper();
		List<Detection> detections = null;
		try (Stream<Path> stream = Files.walk(Paths.get(detectionPath))) {
			detections = stream.filter(Files::isReadable).filter(p -> !Files.isDirectory(p)).map(p -> {
				try {
					return objectMapper.readValue(p.toFile(), Detection.class);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}).collect(Collectors.toList());
		}
		return detections;
	}

	private String getDetectionPath(String camera, LocalDateTime timestamp) {
		String path = DETECTION_PATH_PREFIX + ImageUtils.getImagePath(camera, timestamp);
		File file = new File(path);
		file.mkdirs();
		return path;
	}
}
