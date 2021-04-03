package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.service.HousekeepingService;

@Service
public class HousekeepingServiceImpl implements HousekeepingService {

	@Value("${filesystem.media.path}")
	private String mediaPath;

	@Value("${filesystem.remote.path}")
	private String remotePath;

	private static final Logger logger = LoggerFactory.getLogger(HousekeepingServiceImpl.class);

	@Override
	public void runHousekeeping() {
		logger.info("Running Housekeeping...");
		getPercentageDiskSpaceFree(mediaPath);
		getPercentageDiskSpaceFree(remotePath);
	}

	private double getPercentageDiskSpaceFree(String path) {
		File file = new File(path);
		long totalSpace = file.getTotalSpace();
		long usableSpace = file.getUsableSpace();
		double percentageDiskSpaceFree = 100d * (double) usableSpace / (double) totalSpace;
		logger.info("Total Space for " + path + ": " + totalSpace + " bytes");
		logger.info("Usable Space for " + path + " : " + usableSpace + " bytes");
		logger.info("Percentage Disk Space Free: " + percentageDiskSpaceFree + "%");
		return percentageDiskSpaceFree;
	}
}
