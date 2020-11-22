package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface TestImageService {

	public void setOriginalImage(Image image);

	public void setStampedImage(Image image);

	public Image getOriginalImage();

	public Image getStampedImage();

	public boolean hasOriginalImage();

	public boolean hasStampedImage();
}
