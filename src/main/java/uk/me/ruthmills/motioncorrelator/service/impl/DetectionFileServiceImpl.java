package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
	public List<Detection> readDetections(String camera, int maxDetections) throws IOException {
		return readDetections(camera, LocalDateTime.now().format(TIMESTAMP_FORMAT), maxDetections);
	}

	@Override
	public List<Detection> readDetections(String camera, String timestamp, int maxDetections) throws IOException {
		String year = timestamp.substring(0, 4);
		String month = timestamp.substring(5, 7);
		String day = timestamp.substring(8, 10);
		String hour = timestamp.substring(11, 13);
		List<Detection> detections = new ArrayList<>();
		String path = getDetectionPath(camera, year, month, day, hour);
		logger.info("Detection Path: " + path);
		while (path != null && detections.size() < 50) {
			String[] parts = path.split("/");
			year = parts[0];
			month = parts[1];
			day = parts[2];
			hour = parts[3];
			LocalDateTime dateTime = LocalDateTime.parse(timestamp, TIMESTAMP_FORMAT);
			detections.addAll(readDetections(camera, year, month, day, hour).stream()
					.filter(detection -> detection.getTimestamp().isBefore(dateTime)).collect(Collectors.toList()));
			if (detections.size() < 50) {
				path = getPreviousHour(camera, year, month, day, hour);
			}
		}
		if (detections.size() > maxDetections) {
			detections = new ArrayList<Detection>(detections.subList(0, maxDetections));
		}
		return detections;
	}

	@Override
	public Detection readDetection(String camera, String year, String month, String day, String hour, String timestamp,
			String sequence) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		String filename = DETECTION_PATH_PREFIX + camera + "/" + year + "/" + month + "/" + day + "/" + hour + "/"
				+ timestamp + "-" + sequence + ".json";
		return mapper.readValue(new File(filename), Detection.class);
	}

	private String getClosestMatch(String path, String match) {
		File directory = new File(path);
		List<String> matches = Arrays.asList(directory.list()).stream().sorted(Comparator.reverseOrder())
				.filter(m -> Integer.parseInt(m) <= Integer.parseInt(match)).collect(Collectors.toList());
		if (matches.size() > 0) {
			return matches.get(0);
		} else {
			return null;
		}
	}

	private String getClosestYear(String camera, String year) {
		String path = DETECTION_PATH_PREFIX + camera;
		return getClosestMatch(path, year);
	}

	private String getClosestMonth(String camera, String year, String month) {
		String path = DETECTION_PATH_PREFIX + camera + "/" + year;
		return getClosestMatch(path, month);
	}

	private String getClosestDay(String camera, String year, String month, String day) {
		String path = DETECTION_PATH_PREFIX + camera + "/" + year + "/" + month;
		return getClosestMatch(path, day);
	}

	private String getClosestHour(String camera, String year, String month, String day, String hour) {
		String path = DETECTION_PATH_PREFIX + camera + "/" + year + "/" + month + "/" + day;
		return getClosestMatch(path, hour);
	}

	private String getPreviousYear(String camera, String year) {
		return getDetectionPath(camera, Integer.toString(Integer.parseInt(year) - 1), "12", "31", "23");
	}

	private String getPreviousMonth(String camera, String year, String month) {
		int previousMonth = Integer.parseInt(month) - 1;
		if (previousMonth == 0) {
			return getPreviousYear(camera, year);
		} else if (previousMonth < 10) {
			return getDetectionPath(camera, year, "0" + Integer.toString(previousMonth), "31", "23");
		} else {
			return getDetectionPath(camera, year, Integer.toString(previousMonth), "31", "23");
		}
	}

	private String getPreviousDay(String camera, String year, String month, String day) {
		int previousDay = Integer.parseInt(day) - 1;
		if (previousDay == 0) {
			return getPreviousMonth(camera, year, month);
		} else if (previousDay < 10) {
			return getDetectionPath(camera, year, month, "0" + Integer.toString(previousDay), "23");
		} else {
			return getDetectionPath(camera, year, month, Integer.toString(previousDay), "23");
		}
	}

	private String getPreviousHour(String camera, String year, String month, String day, String hour) {
		int previousHour = Integer.parseInt(hour) - 1;
		if (previousHour < 0) {
			return getPreviousDay(camera, year, month, day);
		} else if (previousHour < 10) {
			return getDetectionPath(camera, year, month, day, "0" + Integer.toString(previousHour));
		} else {
			return getDetectionPath(camera, year, month, day, Integer.toString(previousHour));
		}
	}

	private String getDetectionPath(String camera, String year, String month, String day, String hour) {
		String closestYear = getClosestYear(camera, year);
		if (closestYear == null) {
			return null;
		} else if (!closestYear.equals(year)) {
			return getPreviousYear(camera, year);
		} else {
			String closestMonth = getClosestMonth(camera, year, month);
			if (closestMonth == null) {
				return getPreviousYear(camera, year);
			} else if (!closestMonth.equals(month)) {
				return getPreviousMonth(camera, year, month);
			} else {
				String closestDay = getClosestDay(camera, year, month, day);
				if (closestDay == null) {
					return getPreviousMonth(camera, year, month);
				} else if (!closestDay.equals(day)) {
					return getPreviousDay(camera, year, month, day);
				} else {
					String closestHour = getClosestHour(camera, year, month, day, hour);
					if (closestHour == null) {
						return getPreviousDay(camera, year, month, day);
					} else {
						return year + "/" + month + "/" + day + "/" + hour;
					}
				}
			}
		}
	}

	private List<Detection> readDetections(String camera, String year, String month, String day, String hour)
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
