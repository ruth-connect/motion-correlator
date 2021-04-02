package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.Detection;

public interface ImageFileService {

	public void writeImages(Detection detection, boolean remote) throws IOException;

	public void writeImage(String camera, LocalDateTime timestamp, long sequence, byte[] image, boolean remote)
			throws IOException;

	public void writeImage(String camera, LocalDateTime timestamp, long sequence, byte[] image, String suffix,
			boolean remote) throws IOException;

	public byte[] readImage(String camera, String year, String month, String day, String hour, String filename)
			throws IOException;
}
