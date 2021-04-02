package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.DetectionDates;

public interface DetectionFileService {

	public void writeDetection(Detection detection) throws IOException, JsonMappingException, JsonGenerationException;

	public List<Detection> readDetections(String camera, int maxDetections) throws IOException;

	public List<Detection> readDetections(String camera, String timestamp, int maxDetections) throws IOException;

	public Detection readDetection(String camera, String year, String month, String day, String hour, String timestamp,
			String sequence) throws IOException;

	public DetectionDates getDetectionDates(String camera) throws IOException;

	public DetectionDates getDetectionDates(String camera, String year) throws IOException;

	public DetectionDates getDetectionDates(String camera, String year, String month) throws IOException;

	public DetectionDates getDetectionDates(String camera, String year, String month, String day) throws IOException;

	public DetectionDates getDetectionDates(String camera, String year, String month, String day, String hour)
			throws IOException;

	public DetectionDates getDetectionDates(String camera, String year, String month, String day, String hour,
			String minute) throws IOException;
}
