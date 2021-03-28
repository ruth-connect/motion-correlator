package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.service.DetectionFileService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class DetectionFileServiceImpl implements DetectionFileService {

	private static final String DETECTION_PATH_PREFIX = "/mnt/media/detections/";
	private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");
	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");
	private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd");

	private static final Logger logger = LoggerFactory.getLogger(DetectionFileServiceImpl.class);

	@Override
	public void writeDetection(Detection detection) throws IOException, JsonMappingException, JsonGenerationException {
		String detectionFilename = getDetectionPath(detection.getCamera(), detection.getTimestamp()) + "/"
				+ detection.getTimestamp() + "-" + detection.getSequence() + ".json";
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.writeValue(new File(detectionFilename), detection);
	}

	@Override
	public List<Detection> readDetectionsForToday(String camera) throws IOException {
		LocalDateTime now = LocalDateTime.now();
		String year = now.format(YEAR_FORMAT);
		String month = now.format(MONTH_FORMAT);
		String day = now.format(DAY_FORMAT);
		return readDetections(camera, year, month, day);
	}

	@Override
	public List<Detection> readDetections(String camera, String year, String month, String day) throws IOException {
		String detectionPath = DETECTION_PATH_PREFIX + camera + "/" + year + "/" + month + "/" + day;
		return readDetections(detectionPath);
	}

	@Override
	public List<Detection> readDetections(String camera, String year, String month, String day, String hour)
			throws IOException {
		String detectionPath = DETECTION_PATH_PREFIX + camera + "/" + year + "/" + month + "/" + day + "/" + hour;
		return readDetections(detectionPath);
	}

	private List<Detection> readDetections(String detectionPath) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		List<Detection> detections = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Paths.get(detectionPath))) {
			detections = stream.filter(Files::isReadable).filter(p -> !Files.isDirectory(p)).map(p -> {
				try {
					return mapper.readValue(p.toFile(), Detection.class);
				} catch (Exception ex) {
					logger.error("Failed to read detection from JSON file: " + p.toString(), ex);
					return null;
				}
			}).filter(p -> p != null).sorted(Comparator.comparing(Detection::getTimestamp).reversed())
					.collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error("Failed to read detections", ex);
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
