package uk.me.ruthmills.motioncorrelator.service.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.ObjectDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;

@Service
public class ImageStampingServiceImpl implements ImageStampingService {

	@Override
	public void stampImage(MotionCorrelation motionCorrelation) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(motionCorrelation.getImage().getBytes());
		BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		drawPersonDetection(graphics2D, motionCorrelation.getPersonDetection());
		graphics2D.dispose();
		Image stampedImage = new Image();
		stampedImage.setTimestamp(motionCorrelation.getImage().getTimestamp());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
		stampedImage.setBytes(byteArrayOutputStream.toByteArray());
		motionCorrelation.setStampedImage(stampedImage);
	}

	private void drawPersonDetection(Graphics2D graphics2D, PersonDetection personDetection) {
		drawObjectDetections(graphics2D, personDetection.getFrontalFaceDetections(), Color.RED);
		drawObjectDetections(graphics2D, personDetection.getProfileFaceDetections(), Color.ORANGE);
		drawObjectDetections(graphics2D, personDetection.getUpperBodyDetections(), Color.YELLOW);
		drawObjectDetections(graphics2D, personDetection.getLowerBodyDetections(), Color.GREEN);
		drawObjectDetections(graphics2D, personDetection.getFullBodyDetections(), Color.CYAN);
	}

	private void drawObjectDetections(Graphics2D graphics2D, List<ObjectDetection> objectDetections, Color color) {
		for (ObjectDetection objectDetection : objectDetections) {
			drawObjectDetection(graphics2D, objectDetection, color);
		}
	}

	private void drawObjectDetection(Graphics2D graphics2D, ObjectDetection objectDetection, Color color) {
		graphics2D.setColor(color);
		graphics2D.drawRect(objectDetection.getLeft(), objectDetection.getTop(), objectDetection.getWidth(),
				objectDetection.getHeight());
	}
}
