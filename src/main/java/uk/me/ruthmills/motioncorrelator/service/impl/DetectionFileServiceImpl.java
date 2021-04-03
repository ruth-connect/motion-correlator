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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.DetectionDates;
import uk.me.ruthmills.motioncorrelator.service.DetectionFileService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class DetectionFileServiceImpl implements DetectionFileService {

	@Value("${filesystem.media.path}")
	private String mediaPath;

	@Value("${filesystem.remote.path}")
	private String remotePath;

	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static final Logger logger = LoggerFactory.getLogger(DetectionFileServiceImpl.class);

	@Override
	public void writeDetection(Detection detection, boolean remote)
			throws IOException, JsonMappingException, JsonGenerationException {
		String detectionFilename = getDetectionPath(detection.getCamera(), detection.getTimestamp(), remote) + "/"
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
		logger.info("Read Detections. Camera: " + camera + ", Timestamp: " + timestamp + ", Max Detections: "
				+ maxDetections);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String year = timestamp.substring(0, 4);
		String month = timestamp.substring(5, 7);
		String day = timestamp.substring(8, 10);
		String hour = timestamp.substring(11, 13);
		List<Detection> detections = new ArrayList<>();
		String path = getDetectionPath(camera, year, month, day, hour);
		while (path != null && detections.size() < 50) {
			logger.info("Detection Path: " + path);
			String[] parts = path.split("/");
			year = parts[0];
			month = parts[1];
			day = parts[2];
			hour = parts[3];
			LocalDateTime dateTime = LocalDateTime.parse(timestamp, TIMESTAMP_FORMAT);
			detections
					.addAll(readDetections(camera, year, month, day, hour, timestamp, maxDetections - detections.size())
							.stream().filter(detection -> detection.getTimestamp().isBefore(dateTime))
							.collect(Collectors.toList()));
			logger.info("Detections size: " + detections.size());
			if (detections.size() < 50) {
				path = getPreviousHour(camera, year, month, day, hour);
			}
		}
		stopWatch.stop();
		logger.info("Read time: " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
		return detections;
	}

	@Override
	public Detection readDetection(String camera, String year, String month, String day, String hour, String timestamp,
			String sequence) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		String filename = getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour
				+ "/" + timestamp + "-" + sequence + ".json";
		return mapper.readValue(new File(filename), Detection.class);
	}

	public DetectionDates getDetectionDates(String camera) throws IOException {
		String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
		String year = timestamp.substring(0, 4);
		String month = timestamp.substring(5, 7);
		String day = timestamp.substring(8, 10);
		String hour = timestamp.substring(11, 13);

		String path = getDetectionPath(camera, year, month, day, hour);
		logger.info("Getting detection dates for NOW for path: " + path);
		String[] parts = path.split("/");
		year = parts[0];
		month = parts[1];
		day = parts[2];
		hour = parts[3];

		DetectionDates detectionDates = new DetectionDates();
		detectionDates.setYears(getDirectoryNames(getDetectionPathPrefix(false) + camera));
		detectionDates.setMonths(getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year));
		detectionDates.setDays(getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month));
		detectionDates.setHours(
				getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day));
		detectionDates.setMinutes(
				getMinutes(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour));
		detectionDates.setSeconds(
				getSeconds(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour,
						detectionDates.getMinutes().get(0)));
		return detectionDates;
	}

	public DetectionDates getDetectionDates(String camera, String year) throws IOException {
		String path = getDetectionPath(camera, year, "12", "31", "23");
		logger.info("Getting detection dates for YEAR for path: " + path);
		String[] parts = path.split("/");
		year = parts[0];
		String month = parts[1];
		String day = parts[2];
		String hour = parts[3];

		DetectionDates detectionDates = new DetectionDates();
		detectionDates.setMonths(getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year));
		detectionDates.setDays(getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month));
		detectionDates.setHours(
				getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day));
		detectionDates.setMinutes(
				getMinutes(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour));
		detectionDates.setSeconds(
				getSeconds(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour,
						detectionDates.getMinutes().get(0)));
		return detectionDates;

	}

	public DetectionDates getDetectionDates(String camera, String year, String month) throws IOException {
		String path = getDetectionPath(camera, year, month, "31", "23");
		logger.info("Getting detection dates for MONTH for path: " + path);
		String[] parts = path.split("/");
		year = parts[0];
		month = parts[1];
		String day = parts[2];
		String hour = parts[3];

		DetectionDates detectionDates = new DetectionDates();
		detectionDates.setDays(getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month));
		detectionDates.setHours(
				getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day));
		detectionDates.setMinutes(
				getMinutes(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour));
		detectionDates.setSeconds(
				getSeconds(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour,
						detectionDates.getMinutes().get(0)));
		return detectionDates;

	}

	public DetectionDates getDetectionDates(String camera, String year, String month, String day) throws IOException {
		String path = getDetectionPath(camera, year, month, day, "23");
		logger.info("Getting detection dates for DAY for path: " + path);
		String[] parts = path.split("/");
		year = parts[0];
		month = parts[1];
		day = parts[2];
		String hour = parts[3];

		DetectionDates detectionDates = new DetectionDates();
		detectionDates.setHours(
				getDirectoryNames(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day));
		detectionDates.setMinutes(
				getMinutes(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour));
		detectionDates.setSeconds(
				getSeconds(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour,
						detectionDates.getMinutes().get(0)));
		return detectionDates;
	}

	public DetectionDates getDetectionDates(String camera, String year, String month, String day, String hour)
			throws IOException {
		String path = getDetectionPath(camera, year, month, day, hour);
		logger.info("Getting detection dates for HOUR for path: " + path);
		String[] parts = path.split("/");
		year = parts[0];
		month = parts[1];
		day = parts[2];
		hour = parts[3];

		DetectionDates detectionDates = new DetectionDates();
		detectionDates.setMinutes(
				getMinutes(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour));
		detectionDates.setSeconds(
				getSeconds(getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour,
						detectionDates.getMinutes().get(0)));
		return detectionDates;
	}

	public DetectionDates getDetectionDates(String camera, String year, String month, String day, String hour,
			String minute) throws IOException {
		String path = getDetectionPath(camera, year, month, day, hour);
		logger.info("Getting detection dates for MINUTE for path: " + path);
		String[] parts = path.split("/");
		year = parts[0];
		month = parts[1];
		day = parts[2];
		hour = parts[3];

		DetectionDates detectionDates = new DetectionDates();
		detectionDates.setSeconds(getSeconds(
				getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/" + hour, minute));
		return detectionDates;
	}

	private List<String> getDirectoryNames(String path) {
		File directory = new File(path);
		if (directory.exists()) {
			List<String> directoryNames = Arrays.asList(directory.list());
			Collections.sort(directoryNames, Collections.reverseOrder());
			return directoryNames;
		} else {
			return Collections.<String>emptyList();
		}
	}

	private List<String> getMinutes(String path) {
		List<String> minutes = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Paths.get(path))) {
			minutes = stream.filter(Files::isReadable).filter(p -> !Files.isDirectory(p)).map(p -> {
				try {
					return p.toFile().getName().substring(14, 16);
				} catch (Exception ex) {
					logger.error("Failed get minutes from filename: " + p.toString(), ex);
					return null;
				}
			}).filter(p -> p != null).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error("Failed to read minutes", ex);
		}
		return minutes;
	}

	private List<String> getSeconds(String path, String minute) {
		List<String> seconds = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Paths.get(path))) {
			seconds = stream.filter(Files::isReadable).filter(p -> {
				try {
					return !Files.isDirectory(p) && p.toFile().getName().substring(14, 16).equals(minute);
				} catch (Exception ex) {
					logger.error("Failed to filter minutes from filename: " + p.toString(), ex);
					return false;
				}
			}).map(p -> {
				try {
					return p.toFile().getName().substring(17, 19);
				} catch (Exception ex) {
					logger.error("Failed get seconds from filename: " + p.toString(), ex);
					return null;
				}
			}).filter(p -> p != null).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error("Failed to read seconds", ex);
		}
		return seconds;
	}

	private String getClosestMatch(String path, String match) {
		File directory = new File(path);
		if (directory.exists()) {
			List<String> matches = Arrays.asList(directory.list()).stream().sorted(Comparator.reverseOrder())
					.filter(m -> Integer.parseInt(m) <= Integer.parseInt(match)).collect(Collectors.toList());
			if (matches.size() > 0) {
				return matches.get(0);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private String getDetectionPathPrefix(boolean remote) {
		return (remote ? remotePath : mediaPath) + "/detections/";
	}

	private String getClosestYear(String camera, String year) {
		logger.info("getClosestYear: " + camera + "/" + year);
		String path = getDetectionPathPrefix(false) + camera;
		return getClosestMatch(path, year);
	}

	private String getClosestMonth(String camera, String year, String month) {
		logger.info("getClosestMonth: " + camera + "/" + year + "/" + month);
		if (month.length() < 2) {
			month = "0" + month;
		}
		String path = getDetectionPathPrefix(false) + camera + "/" + year;
		return getClosestMatch(path, month);
	}

	private String getClosestDay(String camera, String year, String month, String day) {
		logger.info("getClosestDay: " + camera + "/" + year + "/" + month + "/" + day);
		if (day.length() < 2) {
			day = "0" + day;
		}
		String path = getDetectionPathPrefix(false) + camera + "/" + year + "/" + month;
		return getClosestMatch(path, day);
	}

	private String getClosestHour(String camera, String year, String month, String day, String hour) {
		logger.info("getClosestHour: " + camera + "/" + year + "/" + month + "/" + day + "/" + hour);
		if (hour.length() < 2) {
			hour = "0" + hour;
		}
		String path = getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day;
		return getClosestMatch(path, hour);
	}

	private String getPreviousYear(String camera, String year) {
		return getDetectionPath(camera, Integer.toString(Integer.parseInt(year) - 1), "12", "31", "23");
	}

	private String getPreviousMonth(String camera, String year, String month) {
		int previousMonth = Integer.parseInt(month) - 1;
		if (previousMonth == 0) {
			return getPreviousYear(camera, year);
		} else {
			return getDetectionPath(camera, year, getClosestMonth(camera, year, Integer.toString(previousMonth)), "31",
					"23");
		}
	}

	private String getPreviousDay(String camera, String year, String month, String day) {
		int previousDay = Integer.parseInt(day) - 1;
		if (previousDay == 0) {
			return getPreviousMonth(camera, year, month);
		} else {
			return getDetectionPath(camera, year, month,
					getClosestDay(camera, year, month, Integer.toString(previousDay)), "23");
		}
	}

	private String getPreviousHour(String camera, String year, String month, String day, String hour) {
		int previousHour = Integer.parseInt(hour) - 1;
		if (previousHour < 0) {
			return getPreviousDay(camera, year, month, day);
		} else {
			return getDetectionPath(camera, year, month, day,
					getClosestHour(camera, year, month, day, Integer.toString(previousHour)));
		}
	}

	private String getDetectionPath(String camera, String year, String month, String day, String hour) {
		String closestYear = getClosestYear(camera, year);
		if (closestYear == null) {
			return null;
		}
		if (!closestYear.equals(year) || month == null) {
			return getPreviousYear(camera, year);
		}
		String closestMonth = getClosestMonth(camera, year, month);
		if (closestMonth == null) {
			return getPreviousYear(camera, year);
		}
		if (!closestMonth.equals(month) || day == null) {
			return getPreviousMonth(camera, year, month);
		}
		String closestDay = getClosestDay(camera, year, month, day);
		if (closestDay == null) {
			return getPreviousMonth(camera, year, month);
		}
		if (!closestDay.equals(day) || hour == null) {
			return getPreviousDay(camera, year, month, day);
		}
		String closestHour = getClosestHour(camera, year, month, day, hour);
		if (closestHour == null) {
			return getPreviousDay(camera, year, month, day);
		}
		return year + "/" + month + "/" + day + "/" + closestHour;
	}

	private List<Detection> readDetections(String camera, String year, String month, String day, String hour,
			String timestamp, int maxDetections) throws IOException {
		String detectionPath = getDetectionPathPrefix(false) + camera + "/" + year + "/" + month + "/" + day + "/"
				+ hour;
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		List<Detection> detections = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Paths.get(detectionPath))) {
			detections = stream.filter(Files::isReadable).filter(p -> !Files.isDirectory(p))
					.sorted((p1, p2) -> (p2.toFile().getName().compareTo(p1.toFile().getName()))).filter(p -> {
						String fileTimestamp = p.toFile().getName().substring(0, 23);
						return timestamp.compareTo(fileTimestamp) > 0;
					}).limit(maxDetections).map(p -> {
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

	private String getDetectionPath(String camera, LocalDateTime timestamp, boolean remote) {
		String path = getDetectionPathPrefix(remote) + ImageUtils.getImagePath(camera, timestamp);
		File file = new File(path);
		file.mkdirs();
		return path;
	}
}
