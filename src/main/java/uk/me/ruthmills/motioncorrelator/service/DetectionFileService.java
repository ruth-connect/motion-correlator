package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.me.ruthmills.motioncorrelator.model.Detection;

public interface DetectionFileService {

	public void writeDetection(Detection detection) throws IOException, JsonMappingException, JsonGenerationException;

	public List<Detection> readDetectionsForToday(String camera) throws IOException;

	public List<Detection> readDetections(String camera, String year, String month, String day) throws IOException;

	public List<Detection> readDetections(String camera, String year, String month, String day, String hour)
			throws IOException;

	public Detection readDetection(String camera, String year, String month, String day, String hour, String timestamp,
			String sequence) throws IOException;
}
