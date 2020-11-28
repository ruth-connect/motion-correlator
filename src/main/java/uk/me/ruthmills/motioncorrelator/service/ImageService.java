package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.net.URISyntaxException;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;

public interface ImageService {

	public Image readImage(String camera) throws IOException, URISyntaxException;

	public void writeImage(String camera, Image image) throws IOException;

	public void writeImage(String camera, Image image, PersonDetections personDetections) throws IOException;

	public void writeImage(String camera, Image image, String suffix) throws IOException;
}
