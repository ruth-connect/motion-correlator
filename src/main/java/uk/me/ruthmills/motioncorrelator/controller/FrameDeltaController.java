package uk.me.ruthmills.motioncorrelator.controller;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.me.ruthmills.motioncorrelator.model.image.Frame;
import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.service.FrameService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Controller
@RequestMapping("frameDelta")
public class FrameDeltaController {

	@Autowired
	private FrameService frameService;

	@GetMapping(value = "/{camera}", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getFrameDelta(@PathVariable String camera) {
		Frame frame = frameService.getLatestFrame(camera);
		if (frame != null) {
			Mat averageFrame = frame.getPreviousFrame().getAverageFrame();
			Mat blurredFrame = frame.getBlurredFrame();
			Mat absBlurredFrame = new Mat();
			Mat absAverageFrame = new Mat();
			Core.convertScaleAbs(blurredFrame, absBlurredFrame);
			Core.convertScaleAbs(averageFrame, absAverageFrame);
			Mat frameDelta = new Mat();
			Core.absdiff(absBlurredFrame, absAverageFrame, frameDelta);
			absBlurredFrame.release();
			absAverageFrame.release();
			Image delta = new Image(frame.getSequence(), frame.getTimestamp(), ImageUtils.encodeImage(frameDelta));
			frameDelta.release();
			return delta.getBytes();
		} else {
			return null;
		}
	}
}
