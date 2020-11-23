package uk.me.ruthmills.motioncorrelator.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class AverageFrameServiceImpl implements AverageFrameService {

	private Map<String, Mat> averageFrames = new ConcurrentHashMap<>();

	@Autowired
	private MjpegStreamService mjpegStreamService;

	@Override
	public void addCurrentFrame(String camera) {
		Image image = mjpegStreamService.getLatestImage(camera);
		Mat frame = ImageUtils.decodeImage(image, CvType.CV_32F);
		Mat blurredFrame = new Mat();
		Imgproc.GaussianBlur(frame, blurredFrame, new Size(25, 25), 0d);
		frame.release();
		Mat averageFrame = averageFrames.get(camera);
		if (averageFrame == null) {
			averageFrames.put(camera, blurredFrame);
		} else {
			Imgproc.accumulateWeighted(blurredFrame, averageFrame, 0.5d);
		}
	}

	@Override
	public Image getAverageFrame(String camera) {
		Mat averageFrame = averageFrames.get(camera);
		if (averageFrame == null) {
			return null;
		}
		return ImageUtils.encodeImage(averageFrame);
	}
}
