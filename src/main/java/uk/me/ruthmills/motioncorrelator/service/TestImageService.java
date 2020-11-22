package uk.me.ruthmills.motioncorrelator.service;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

@Service
public interface TestImageService {

	public void setImage(Image image);

	public Image getImage();

	public boolean hasImage();
}
