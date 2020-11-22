package uk.me.ruthmills.motioncorrelator.service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public interface TestImageService {

	public void setImage(Image image);

	public Image getImage();

	public boolean hasImage();
}
