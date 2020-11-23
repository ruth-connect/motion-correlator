package uk.me.ruthmills.motioncorrelator.service;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface MjpegStreamService {

	public Image getLatestImage(String camera);

	public Image getImage(String camera, LocalDateTime timestamp);
}
