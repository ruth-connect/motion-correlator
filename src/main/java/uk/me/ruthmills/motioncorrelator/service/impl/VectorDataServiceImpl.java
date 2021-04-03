package uk.me.ruthmills.motioncorrelator.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.vector.Burst;
import uk.me.ruthmills.motioncorrelator.model.vector.ExternalTrigger;
import uk.me.ruthmills.motioncorrelator.model.vector.Timestamp;
import uk.me.ruthmills.motioncorrelator.model.vector.Vector;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorData;
import uk.me.ruthmills.motioncorrelator.model.vector.VectorDataList;
import uk.me.ruthmills.motioncorrelator.service.VectorDataService;

@Service
public class VectorDataServiceImpl implements VectorDataService {

	private static final Logger logger = LoggerFactory.getLogger(VectorDataServiceImpl.class);

	@Override
	public VectorDataList parseVectorData(String camera, String vectorDataString) {
		String[] vectorDataArray = vectorDataString.split("\n");
		List<VectorData> vectorDataLines = new ArrayList<>();
		LocalDateTime timestamp = null;
		for (String line : vectorDataArray) {
			if (line.trim().length() > 0) {
				if (line.startsWith("</motion>")) {
					break;
				}
				VectorData vectorData = parseLine(line.trim().replaceAll("\\s{2,}", " "));
				if (vectorData instanceof Timestamp) {
					timestamp = ((Timestamp) vectorData).getTimestamp();
				} else {
					vectorDataLines.add(vectorData);
				}
			}
		}
		VectorDataList vectorDataList = new VectorDataList(camera, timestamp, vectorDataLines);
		return vectorDataList;
	}

	private VectorData parseLine(String line) {
		if (line.startsWith("<motion")) {
			return parseTimestamp(line);
		} else if (line.startsWith("b")) {
			return parseBurst(line);
		} else if (line.startsWith("e")) {
			return parseExternalTrigger(line);
		} else {
			return parseVector(line);
		}
	}

	private Timestamp parseTimestamp(String line) {
		String[] elements = line.split(" ");
		String timestampString = elements[1];
		logger.info("Timestamp String: " + timestampString);
		long seconds = Long
				.parseLong(timestampString.indexOf(".") > 0 ? timestampString.substring(0, timestampString.indexOf("."))
						: timestampString);
		int nanos = (int) (timestampString.indexOf(".") > 0
				? Long.parseLong(timestampString.substring(timestampString.indexOf(".") + 1, timestampString.length()))
						* 100 * 1000 * 1000 // tenths of seconds converted to nanoseconds
				: 0);
		logger.info("Seconds: " + seconds + ", Nanos: " + nanos);
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(seconds, nanos,
				ZoneId.of("Europe/London").getRules().getOffset(LocalDateTime.now())); // TODO - get from locale.
		logger.info("Local Date Time: " + localDateTime);
		Timestamp timestamp = new Timestamp();
		timestamp.setTimestamp(localDateTime);
		return timestamp;
	}

	private Burst parseBurst(String line) {
		String[] elements = line.split(" ");
		int burstCount = Integer.parseInt(elements[1]);
		logger.info("Burst. Count: " + burstCount);
		Burst burst = new Burst();
		burst.setBurstCount(burstCount);
		return burst;
	}

	private ExternalTrigger parseExternalTrigger(String line) {
		String[] elements = line.split(" ");
		String code = elements[1].replace("-", " ");
		logger.info("External Trigger. Code: " + code);
		ExternalTrigger externalTrigger = new ExternalTrigger();
		externalTrigger.setCode(code);
		return externalTrigger;
	}

	private Vector parseVector(String line) {
		logger.info("Vector: " + line);
		String[] elements = line.split(" ");
		String region = elements[0];
		if (!region.equals("f")) {
			Integer.parseInt(region); // will throw exception if not a number.
		}
		int x = Integer.parseInt(elements[1]);
		int y = Integer.parseInt(elements[2]);
		int dx = Integer.parseInt(elements[3]);
		int dy = Integer.parseInt(elements[4]);
		int magnitude = Integer.parseInt(elements[5]);
		int count = Integer.parseInt(elements[6]);
		Vector vector = new Vector();
		vector.setRegion(region);
		vector.setX(x);
		vector.setY(y);
		vector.setDx(dx);
		vector.setDy(dy);
		vector.setMagnitude(magnitude);
		vector.setCount(count);
		return vector;
	}
}
