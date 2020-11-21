package uk.me.ruthmills.motioncorrelator.service.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
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

	@Override
	public void stampImage(MotionCorrelation motionCorrelation) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(motionCorrelation.getImage().getBytes());
		BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		drawPersonDetection(graphics2D, motionCorrelation.getPersonDetection());
		drawFrameVector(graphics2D, motionCorrelation.getVectorData());
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

	private void drawFrameVector(Graphics2D graphics2D, VectorDataList vectorData) {
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
