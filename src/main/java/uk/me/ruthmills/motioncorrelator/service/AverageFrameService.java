package uk.me.ruthmills.motioncorrelator.service;

import java.time.LocalDateTime;

import org.opencv.core.Mat;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface AverageFrameService {

	public void addCurrentFrame(String camera, Image image);

	public Image getAverageFrameImage(String camera);

	public Mat getAverageFrameMat(String camera);

	public Mat getAverageFrameMatBefore(String camera, LocalDateTime timestamp);
}
