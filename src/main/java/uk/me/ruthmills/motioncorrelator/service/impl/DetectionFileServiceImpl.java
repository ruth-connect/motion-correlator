package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

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

	private String getDetectionPath(String camera, LocalDateTime timestamp) {
		String path = DETECTION_PATH_PREFIX + ImageUtils.getImagePath(camera, timestamp);
		File file = new File(path);
		file.mkdirs();
		return path;
	}
}
