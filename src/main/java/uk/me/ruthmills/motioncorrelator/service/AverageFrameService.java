package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface AverageFrameService {

	public void addCurrentFrame(String camera);

	public Image getAverageFrame(String camera);
}
