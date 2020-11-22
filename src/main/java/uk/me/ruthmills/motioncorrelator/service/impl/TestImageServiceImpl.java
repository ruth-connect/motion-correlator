package uk.me.ruthmills.motioncorrelator.service.impl;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.TestImageService;

@Service
public class TestImageServiceImpl implements TestImageService {
	private Image originalImage;
	private Image stampedImage;

	@Override
	public void setOriginalImage(Image image) {
		this.originalImage = image;
		stampedImage = null;
	}

	@Override
	public void setStampedImage(Image image) {
		this.stampedImage = image;
	}

	@Override
	public Image getOriginalImage() {
		if (originalImage == null) {
			throw new IllegalStateException("Original image is null");
		}
		return originalImage;
	}

	@Override
	public Image getStampedImage() {
		if (stampedImage == null) {
			throw new IllegalStateException("Stamped image is null");
		}
		return stampedImage;
	}

	@Override
	public boolean hasOriginalImage() {
		return originalImage != null;
	}

	@Override
	public boolean hasStampedImage() {
		return stampedImage != null;
	}
}
