package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import uk.me.ruthmills.motioncorrelator.model.Detection;

public interface DetectionFileService {

	public void writeDetection(Detection detection) throws IOException, JsonMappingException, JsonGenerationException;
}
