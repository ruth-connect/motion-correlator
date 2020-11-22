package uk.me.ruthmills.motioncorrelator.service.impl;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

@Service
public class TestImageServiceImpl {
	private Image image;

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		if (image == null) {
			throw new IllegalStateException("Image is null");
		}
		return image;
	}

	public boolean hasImage() {
		return image != null;
	}
}
