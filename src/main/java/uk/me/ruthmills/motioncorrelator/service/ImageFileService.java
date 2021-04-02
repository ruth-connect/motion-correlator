package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface ImageFileService {

	public void writeImage(String camera, Image image, boolean remote) throws IOException;

	public void writeImage(String camera, Image image, String suffix, boolean remote) throws IOException;

	public byte[] readImage(String camera, String year, String month, String day, String hour, String filename)
			throws IOException;
}
