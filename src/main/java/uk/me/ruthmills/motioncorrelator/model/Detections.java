package uk.me.ruthmills.motioncorrelator.model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.util.TimeUtils;

public class Detections {

	private LinkedList<Detection> detections = new LinkedList<>();

	private static final Logger logger = LoggerFactory.getLogger(Detections.class);

	public Detections() {

	}

	public void addDetection(Detection detection) {
		if (detections.contains(detection)) {
			logger.info("List already contains detection for camera: " + detection.getCamera() + " with timestamp: "
					+ detection.getTimestamp() + " and sequence: " + detection.getSequence()
					+ " so removing the old one");
			detections.remove(detection);
		}
		detections.addLast(detection);
		detections.sort(Comparator.comparing(Detection::getSequence));
	}

	public List<Detection> getDetections() {
		return detections;
	}

	public void removeExpiredDetections() {
		if (detections.peekFirst() != null) {
			long nowMilliseconds = TimeUtils.toMilliseconds(LocalDateTime.now());
			long timestampMilliseconds = TimeUtils.toMilliseconds(detections.peekFirst().getTimestamp());
			if (nowMilliseconds - timestampMilliseconds > 60000) { // 1 minute
				logger.info("Removing expired detection: " + detections.peekFirst());
				detections.removeFirst();
				removeExpiredDetections();
			}
		}
	}

	public PersonProbability getPersonProbability() {
		int numPersonDetectionFrames = 0;
		int numVectorDetectionFrames = 0;
		int numBothDetectionFrames = 0;
		double cumulativePersonDetectionWeights = 0d;
		Detection strongestPersonDetection = null;
		ReverseListIterator<Detection> iterator = new ReverseListIterator<>(detections);
		while (iterator.hasNext()) {
			Detection detection = iterator.next();
			if (detection.getPersonDetections() != null) {
				PersonDetection personDetection = detection.getPersonDetections().getStrongestPersonDetection();
				if (personDetection != null) {
					numPersonDetectionFrames++;
					cumulativePersonDetectionWeights += personDetection.getWeight();
					if (detection.getVectorMotionDetection() != null) {
						numBothDetectionFrames++;
					}
					if (strongestPersonDetection == null || personDetection.getWeight() > strongestPersonDetection
							.getStrongestPersonDetectionWeight()) {
						strongestPersonDetection = detection;
					}
				}
			}
			if (detection.getVectorMotionDetection() != null) {
				numVectorDetectionFrames++;
			}
		}
		PersonProbability personProbability = new PersonProbability(strongestPersonDetection, numPersonDetectionFrames,
				numVectorDetectionFrames, numBothDetectionFrames, cumulativePersonDetectionWeights);
		return personProbability;
	}
}
