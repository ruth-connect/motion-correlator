package uk.me.ruthmills.motioncorrelator.service.impl;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.TestImageService;

@Service
public class TestImageServiceImpl implements TestImageService {
	private Image image;

	@Override
	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public Image getImage() {
		if (image == null) {
			throw new IllegalStateException("Image is null");
		}
		return image;
	}

	@Override
	public boolean hasImage() {
		return image != null;
	}
}
