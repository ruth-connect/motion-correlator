package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface MjpegStreamService {

	public Image getLatestImage(String camera);
}
