package uk.me.ruthmills.motioncorrelator.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

public class ImageUtils {

	private static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("/yyyy/MM/dd/HH");

	public static Mat decodeImage(Image image) {
		Mat encoded = new Mat(1, image.getBytes().length, CvType.CV_8U);
		encoded.put(0, 0, image.getBytes());
		Mat decoded = Imgcodecs.imdecode(encoded, Imgcodecs.IMREAD_GRAYSCALE);
		encoded.release();
		return decoded;
	}

	public static Mat decodeImage(Image image, int imageWidthPixels) {
		Mat decoded = decodeImage(image);
		Mat resized = new Mat();
		Size size = new Size(imageWidthPixels, (imageWidthPixels * 3) / 4);
		Imgproc.resize(decoded, resized, size);
		decoded.release();
		return resized;
	}

	public static byte[] encodeImage(Mat decoded) {
		MatOfByte encoded = new MatOfByte();
		Imgcodecs.imencode(".jpg", decoded, encoded);
		byte[] bytes = encoded.toArray();
		encoded.release();
		return bytes;
	}

	public static String getImagePath(String camera, LocalDateTime timestamp) {
		return camera + timestamp.format(DATE_TIME_FORMAT) + "/";
	}
}
