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
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetectionParameters;
import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;
import uk.me.ruthmills.motioncorrelator.service.MjpegStreamService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class AverageFrameServiceImpl implements AverageFrameService {

	@Autowired
	private MjpegStreamService mjpegStreamService;

	private Map<String, Mat> averageFrames = new ConcurrentHashMap<>();

	@Override
	public void addCurrentFrame(String camera) {
		Image image = mjpegStreamService.getLatestImage(camera);
		Mat decoded = ImageUtils.decodeImage(image, new PersonDetectionParameters().getImageWidthPixels());
		Mat frame = new Mat();
		decoded.convertTo(frame, CvType.CV_32F);
		decoded.release();
		Mat blurredFrame = new Mat();
		Imgproc.GaussianBlur(frame, blurredFrame, new Size(25, 25), 0d);
		frame.release();
		Mat averageFrame = averageFrames.get(camera);
		if (averageFrame == null) {
			averageFrames.put(camera, blurredFrame);
		} else {
			Imgproc.accumulateWeighted(blurredFrame, averageFrame, 0.1d);
			blurredFrame.release();
		}
	}

	@Override
	public Image getAverageFrame(String camera) {
		Mat averageFrame = averageFrames.get(camera);
		if (averageFrame == null) {
			return null;
		}
		synchronized (averageFrame) {
			return ImageUtils.encodeImage(averageFrame);
		}
	}

	@Override
	public Mat getAverageFrameMat(String camera) {
		Mat averageFrame = averageFrames.get(camera);
		synchronized (averageFrame) {
			Mat averageFrameCopy = new Mat();
			averageFrame.copyTo(averageFrameCopy);
			return averageFrameCopy;
		}
	}
}
