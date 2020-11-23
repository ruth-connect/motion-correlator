package uk.me.ruthmills.motioncorrelator.service.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;

@Service
public class ImageStampingServiceImpl implements ImageStampingService {

	private static final Polygon ARROW_HEAD = new Polygon();

	static {
		ARROW_HEAD.addPoint(0, 0);
		ARROW_HEAD.addPoint(-5, -10);
		ARROW_HEAD.addPoint(5, -10);
	}

	private Font font;

	@PostConstruct
	public void initialise() {
		font = new Font("Arial", Font.BOLD, 13);
	}

	@Override
	public void stampImage(MotionCorrelation motionCorrelation) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(motionCorrelation.getImage().getBytes());
		BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		drawPersonDetections(graphics2D, motionCorrelation.getPersonDetections());
		drawFrameVector(graphics2D, motionCorrelation.getVectorData());
		drawPersonDetectionWeights(graphics2D, motionCorrelation.getPersonDetections());
		graphics2D.dispose();
		Image stampedImage = new Image();
		stampedImage.setTimestamp(motionCorrelation.getImage().getTimestamp());
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
		stampedImage.setBytes(byteArrayOutputStream.toByteArray());
		motionCorrelation.setStampedImage(stampedImage);
	}

	private void drawPersonDetections(Graphics2D graphics2D, PersonDetections personDetections) {
		for (int i = 0; i < personDetections.getPersonDetections().size(); i++) {
			Color color = getPersonDetectionColor(i);
			drawPersonDetection(graphics2D, personDetections.getPersonDetections().get(i), color);
		}
	}

	private void drawPersonDetectionWeights(Graphics2D graphics2D, PersonDetections personDetections) {
		for (int i = 0; i < personDetections.getPersonDetections().size(); i++) {
			Color color = getPersonDetectionColor(i);
			BigDecimal weight = new BigDecimal(personDetections.getPersonDetections().get(i).getWeight());
			drawText(graphics2D, weight.setScale(3).toString(), 10, 30 + (i * 40), color);
		}
	}

	private Color getPersonDetectionColor(int i) {
		switch (i % 6) {
		case 0:
			return Color.RED;
		case 1:
			return Color.ORANGE;
		case 2:
			return Color.YELLOW;
		case 3:
			return Color.GREEN;
		case 4:
			return Color.CYAN;
		default:
			return Color.BLUE;
		}
	}

	private void drawPersonDetection(Graphics2D graphics2D, PersonDetection personDetection, Color color) {
		if (personDetection.getWeight() > 0) {
			int thickness = (int) Math.ceil(personDetection.getWeight());
			graphics2D.setStroke(new BasicStroke(thickness));
			graphics2D.setColor(color);
			graphics2D.drawRect(personDetection.getLeft(), personDetection.getTop(), personDetection.getWidth(),
					personDetection.getHeight());
		}
	}

	private void drawFrameVector(Graphics2D graphics2D, VectorDataList vectorData) {
		if (vectorData != null) {
			Vector frameVector = vectorData.getFrameVector();
			if (frameVector != null) {
				int x = frameVector.getStartX();
				int y = frameVector.getStartY();
				int endX = frameVector.getEndX();
				int endY = frameVector.getEndY();

				graphics2D.setColor(Color.MAGENTA);

				double angle = Math.atan2(endY - y, endX - x);

				graphics2D.setStroke(new BasicStroke(2));

				graphics2D.drawLine(x, y, (int) (endX - 10 * Math.cos(angle)), (int) (endY - 10 * Math.sin(angle)));

				AffineTransform tx1 = graphics2D.getTransform();

				AffineTransform tx2 = (AffineTransform) tx1.clone();

				tx2.translate(endX, endY);
				tx2.rotate(angle - Math.PI / 2);

				graphics2D.setTransform(tx2);
				graphics2D.fill(ARROW_HEAD);

				graphics2D.setTransform(tx1);
			}
		}
	}

	private void drawText(Graphics2D graphics2D, String text, int x, int y, Color color) {
		graphics2D.setFont(font);
		graphics2D.setColor(color);
		graphics2D.drawString(text, x, y);
	}
}
