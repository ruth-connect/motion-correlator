package uk.me.ruthmills.motioncorrelator.service;

import java.io.IOException;
import java.net.URISyntaxException;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface ImageService {

	public Image readImage(String camera) throws IOException, URISyntaxException;
}
