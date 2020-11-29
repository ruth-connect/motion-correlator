package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

public class Detections {

	private LinkedList<Detection> detections = new LinkedList<>();

	private static final Logger logger = LoggerFactory.getLogger(Detections.class);

	public Detections() {

	}

	public void addDetection(Detection detection) {
		detections.addLast(detection);
		detections.sort(Comparator.comparing(Detection::getSequence));
	}

	public void removeExpiredDetections() {
		if (detections.peekFirst() != null) {
			long nowMilliseconds = TimeUtils.toMilliseconds(LocalDateTime.now());
			long timestampMilliseconds = TimeUtils.toMilliseconds(detections.peekFirst().getTimestamp());
			if (nowMilliseconds - timestampMilliseconds > 60000) {
				logger.info("Removing expired detection: " + detections.peekFirst());
				detections.removeFirst();
				removeExpiredDetections();
			}
		}
	}
}
