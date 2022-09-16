package uk.me.ruthmills.motioncorrelator.service;

import java.time.LocalDateTime;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;

public interface FrameService {

	public Frame getLatestFrame(String camera);

	public Frame getFrame(String camera, LocalDateTime timestamp);
}
