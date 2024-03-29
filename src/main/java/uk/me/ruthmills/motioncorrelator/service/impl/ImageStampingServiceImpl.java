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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.Detection;
import uk.me.ruthmills.motioncorrelator.model.MotionCorrelation;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorMotionDetection;
import uk.me.ruthmills.motioncorrelator.service.ImageStampingService;

@Service
public class ImageStampingServiceImpl implements ImageStampingService {

	private static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
	private static final Color RED = new Color(255, 48, 48);
	private static final Color ORANGE = new Color(255, 137, 54);
	private static final Color BLUE = new Color(92, 87, 255);
	private static final Polygon ARROW_HEAD = new Polygon();

	static {
		ARROW_HEAD.addPoint(0, 0);
		ARROW_HEAD.addPoint(-5, -10);
		ARROW_HEAD.addPoint(5, -10);
	}

	private Font font;

	private static final Logger logger = LoggerFactory.getLogger(ImageStampingServiceImpl.class);

	@PostConstruct
	public void initialise() {
		font = new Font("Arial", Font.BOLD, 18);
	}

	@Override
	public Image stampImage(MotionCorrelation motionCorrelation) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				motionCorrelation.getFrame().getImage().getBytes());
		BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
		Graphics2D graphics2D = bufferedImage.createGraphics();
		drawPersonDetections(graphics2D, motionCorrelation.getPersonDetections());
		drawFrameVector(graphics2D, motionCorrelation.getVectorMotionDetection());
		if (motionCorrelation.getPersonDetections() != null
				&& motionCorrelation.getPersonDetections().getPersonDetections() != null) {
			drawPersonDetectionWeights(graphics2D, motionCorrelation.getPersonDetections());
			drawDetectionTime(graphics2D, motionCorrelation.getPersonDetections().getDetectionTimeMilliseconds());
			drawTimestamp(graphics2D, motionCorrelation.getFrame().getTimestamp(), 0, Color.WHITE);
		} else {
			logger.warn("Null person detections for motion correlation for camera: " + motionCorrelation.getCamera()
					+ " with timestamp: " + motionCorrelation.getFrameTimestamp());
		}
		drawVectorText(graphics2D, motionCorrelation.getVectorMotionDetection());
		graphics2D.dispose();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
		Image stampedImage = new Image(motionCorrelation.getFrame().getSequence(),
				motionCorrelation.getFrame().getTimestamp(), byteArrayOutputStream.toByteArray());
		return stampedImage;
	}

	public byte[] stampImage(Detection detection, byte[] jpeg) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jpeg);
		BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
		Graphics2D graphics2D = null;
		if (bufferedImage.getWidth() == 320) { // resize to 640 x 480.
			BufferedImage resizedImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
			graphics2D = resizedImage.createGraphics();
			graphics2D.drawImage(bufferedImage, 0, 0, 640, 480, null);
			bufferedImage = resizedImage;
		} else {
			graphics2D = bufferedImage.createGraphics();
		}
		drawPersonDetections(graphics2D, detection.getPersonDetections());
		drawFrameVector(graphics2D, detection.getVectorMotionDetection());
		if (detection.getPersonDetections() != null && detection.getPersonDetections().getPersonDetections() != null) {
			drawPersonDetectionWeights(graphics2D, detection.getPersonDetections());
			drawLatency(graphics2D, detection.getLatency());
			drawDetectionTime(graphics2D, detection.getPersonDetections().getDetectionTimeMilliseconds());
			drawTimestamp(graphics2D, detection.getTimestamp(), 0, Color.WHITE);
		} else {
			logger.warn("Null person detections for detection for camera: " + detection.getCamera()
					+ " with timestamp: " + detection.getTimestamp());
		}
		drawVectorText(graphics2D, detection.getVectorMotionDetection());
		graphics2D.dispose();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	private void drawPersonDetections(Graphics2D graphics2D, PersonDetections personDetections) {
		if (personDetections != null && personDetections.getPersonDetections() != null) {
			for (int i = 0; i < personDetections.getPersonDetections().size(); i++) {
				Color color = getPersonDetectionColor(i);
				drawPersonDetection(graphics2D, personDetections.getPersonDetections().get(i), color);
			}
		}
	}

	private void drawPersonDetectionWeights(Graphics2D graphics2D, PersonDetections personDetections) {
		for (int i = 0; i < personDetections.getPersonDetections().size(); i++) {
			Color color = getPersonDetectionColor(i);
			BigDecimal weight = new BigDecimal(personDetections.getPersonDetections().get(i).getWeight());
			drawText(graphics2D, weight.setScale(3, RoundingMode.HALF_UP).toString(), 10, 30 + (i * 40), color);
		}
	}

	private void drawLatency(Graphics2D graphics2D, int latency) {
		drawText(graphics2D, "Lat: " + latency + "ms", 10, 370, Color.WHITE);
	}

	private void drawDetectionTime(Graphics2D graphics2D, long detectionTimeMilliseconds) {
		drawText(graphics2D, "Det: " + detectionTimeMilliseconds + "ms", 10, 410, Color.WHITE);
	}

	private Color getPersonDetectionColor(int i) {
		switch (i % 6) {
		case 0:
			return RED;
		case 1:
			return ORANGE;
		case 2:
			return Color.YELLOW;
		case 3:
			return Color.GREEN;
		case 4:
			return Color.CYAN;
		default:
			return BLUE;
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

	private void drawFrameVector(Graphics2D graphics2D, VectorMotionDetection vectorMotionDetection) {
		if (vectorMotionDetection != null) {
			Vector frameVector = vectorMotionDetection.getFrameVector();
			if (frameVector != null) {
				drawFrameVector(graphics2D, frameVector, -1, -1, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, 0, -1, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, 1, -1, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, -1, 0, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, 1, 0, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, -1, 1, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, 0, 1, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, 1, 1, Color.BLACK);
				drawFrameVector(graphics2D, frameVector, 0, 0, Color.MAGENTA);
			}
		}
	}

	private void drawFrameVector(Graphics2D graphics2D, Vector frameVector, int offsetX, int offsetY, Color color) {
		int startX = frameVector.getStartX() + offsetX;
		int startY = frameVector.getStartY() + offsetY;
		int endX = frameVector.getEndX() + offsetX;
		int endY = frameVector.getEndY() + offsetY;

		graphics2D.setColor(color);

		double angle = Math.atan2(endY - startY, endX - startX);

		graphics2D.setStroke(new BasicStroke(2));

		graphics2D.drawLine(startX, startY, (int) (endX - 10 * Math.cos(angle)), (int) (endY - 10 * Math.sin(angle)));

		AffineTransform tx1 = graphics2D.getTransform();

		AffineTransform tx2 = (AffineTransform) tx1.clone();

		tx2.translate(endX, endY);
		tx2.rotate(angle - Math.PI / 2);

		graphics2D.setTransform(tx2);
		graphics2D.fill(ARROW_HEAD);

		graphics2D.setTransform(tx1);
	}

	private void drawVectorText(Graphics2D graphics2D, VectorMotionDetection vectorMotionDetection) {
		if (vectorMotionDetection != null) {
			LocalDateTime vectorTimestamp = vectorMotionDetection.getTimestamp();
			Vector frameVector = vectorMotionDetection.getFrameVector();
			if (vectorTimestamp != null) {
				drawTimestamp(graphics2D, vectorTimestamp, 1, Color.MAGENTA);
			}
			if (frameVector != null) {
				drawText(graphics2D, "Mag: " + frameVector.getMagnitude(), 530, 410, Color.MAGENTA);
				drawText(graphics2D, "Cnt: " + frameVector.getCount(), 530, 450, Color.MAGENTA);
			}
			if (vectorMotionDetection.isInterpolated()) {
				drawText(graphics2D, "[Int]", 530, 370, Color.MAGENTA);
			}
		}
	}

	private void drawTimestamp(Graphics2D graphics2D, LocalDateTime timestamp, int index, Color color) {
		drawText(graphics2D, timestamp.format(TIME_FORMAT), 490, 30 + (index * 40), color);
	}

	private void drawText(Graphics2D graphics2D, String text, int x, int y, Color color) {
		graphics2D.setFont(font);
		graphics2D.setColor(Color.BLACK);
		graphics2D.drawString(text, x - 1, y - 1);
		graphics2D.drawString(text, x, y - 1);
		graphics2D.drawString(text, x + 1, y - 1);
		graphics2D.drawString(text, x - 1, y);
		graphics2D.drawString(text, x + 1, y);
		graphics2D.drawString(text, x - 1, y + 1);
		graphics2D.drawString(text, x, y + 1);
		graphics2D.drawString(text, x + 1, y + 1);
		graphics2D.setColor(color);
		graphics2D.drawString(text, x, y);
	}
}
