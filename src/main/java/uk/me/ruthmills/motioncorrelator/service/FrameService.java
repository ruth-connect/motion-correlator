package uk.me.ruthmills.motioncorrelator.service;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface FrameService {

	public void addCurrentFrame(String camera, Image image);

	public Frame getLatestFrame(String camera);

	public Frame getFrame(String camera, LocalDateTime timestamp);
}
