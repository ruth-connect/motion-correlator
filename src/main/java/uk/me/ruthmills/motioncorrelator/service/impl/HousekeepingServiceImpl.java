package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import uk.me.ruthmills.motioncorrelator.service.HousekeepingService;

@Service
public class HousekeepingServiceImpl implements HousekeepingService {

	@Value("${filesystem.media.path}")
	private String mediaPath;

	@Value("${filesystem.remote.path}")
	private String remotePath;

	@Value("${filesystem.media.min.percent.free}")
	private double mediaMinPercentFree;

	@Value("${filesystem.remote.min.percent.free}")
	private double remoteMinPercentFree;

	private static final Logger logger = LoggerFactory.getLogger(HousekeepingServiceImpl.class);

	@Override
	public void runHousekeeping() {
		logger.info("Running Housekeeping...");
		double mediaPercentFree = getPercentageDiskSpaceFree(mediaPath);
		double remotePercentFree = getPercentageDiskSpaceFree(remotePath);
		if (mediaPercentFree < mediaMinPercentFree) {
			logger.info("Media percent free is below minimum of " + mediaMinPercentFree + "%");
			freeDiskSpace(mediaPath, mediaPercentFree, mediaMinPercentFree);
		}
		if (remotePercentFree < remoteMinPercentFree) {
			logger.info("Remote percent free is below minimum of " + remoteMinPercentFree + "%");
			freeDiskSpace(remotePath, remotePercentFree, remoteMinPercentFree);
		}
	}

	private double getPercentageDiskSpaceFree(String path) {
		File file = new File(path);
		long totalSpace = file.getTotalSpace();
		long usableSpace = file.getUsableSpace();
		double percentageDiskSpaceFree = 100d * (double) usableSpace / (double) totalSpace;
		logger.info("Total Space for " + path + ": " + totalSpace + " bytes");
		logger.info("Usable Space for " + path + " : " + usableSpace + " bytes");
		logger.info("Percentage Disk Space Free for " + path + " : " + percentageDiskSpaceFree + "%");
		return percentageDiskSpaceFree;
	}

	private void freeDiskSpace(String path, double percentFree, double minPercentFree) {
		do {
			logger.info("Freeing disk space for: " + path);
			String earliestDay = getEarliestDay(path);
			logger.info("Earliest day to free disk space for: " + earliestDay);
			freeDiskSpaceForDay(path, earliestDay);
			percentFree = getPercentageDiskSpaceFree(path);
			logger.info("Disk space free for " + path + " now: " + percentFree);
		} while (percentFree < minPercentFree);
	}

	private void freeDiskSpaceForDay(String path, String day) {
		File topLevelDirectory = new File(path);
		for (File mediaTypeDirectory : topLevelDirectory.listFiles()) {
			if (mediaTypeDirectory.isDirectory()) {
				String mediaType = mediaTypeDirectory.getName();
				List<String> cameras = Arrays.asList(mediaTypeDirectory.list());
				for (String camera : cameras) {
					File cameraDirectory = new File(path + "/" + mediaType + "/" + camera);
					if (cameraDirectory.isDirectory()) {
						String dayPath = path + "/" + mediaType + "/" + camera + "/" + day;
						File dayDirectoryToDelete = new File(dayPath);
						if (dayDirectoryToDelete.exists()) {
							logger.info("Deleting DAY: " + dayPath);
							FileSystemUtils.deleteRecursively(dayDirectoryToDelete);

							String monthPath = dayPath.substring(0, dayPath.lastIndexOf("/"));
							File monthDirectory = new File(monthPath);
							if (monthDirectory.list().length == 0) {
								logger.info("Deleting MONTH: " + monthPath);
								monthDirectory.delete();

								String yearPath = monthPath.substring(0, monthPath.lastIndexOf("/"));
								File yearDirectory = new File(yearPath);
								if (yearDirectory.list().length == 0) {
									logger.info("Deleting YEAR: " + yearPath);
									yearDirectory.delete();
								}
							}
						}
					}
				}
			}
		}
	}

	private String getEarliestDay(String path) {
		List<String> earliestDays = new ArrayList<>();
		File topLevelDirectory = new File(path);
		for (File mediaTypeDirectory : topLevelDirectory.listFiles()) {
			if (mediaTypeDirectory.isDirectory()) {
				String mediaType = mediaTypeDirectory.getName();
				List<String> cameras = Arrays.asList(mediaTypeDirectory.list());
				for (String camera : cameras) {
					File cameraDirectory = new File(path + "/" + mediaType + "/" + camera);
					if (cameraDirectory.isDirectory()) {
						String earliestDay = getEarliestDayForCamera(path, mediaType, camera, cameraDirectory);
						if (earliestDay != null) {
							earliestDays.add(earliestDay);
						}
					}
				}
			}
		}
		if (earliestDays.size() == 0) {
			return null;
		} else {
			Collections.sort(earliestDays);
			return earliestDays.get(0);
		}
	}

	private String getEarliestDayForCamera(String path, String mediaType, String camera, File cameraDirectory) {
		List<String> years = Arrays.asList(cameraDirectory.list());
		Collections.sort(years);
		if (years.size() > 0) {
			int yearIndex = 0;
			String earliestMonth = "";
			while (yearIndex < years.size() && earliestMonth.equals("")) {
				String earliestYear = years.get(yearIndex);
				File yearDirectory = new File(path + "/" + mediaType + "/" + camera + "/" + earliestYear);
				if (!yearDirectory.isDirectory()) {
					yearIndex++;
				} else {
					List<String> months = Arrays.asList(yearDirectory.list());
					Collections.sort(months);
					if (months.size() > 0) {
						int monthIndex = 0;
						String earliestDay = "";
						while (monthIndex < months.size() && earliestDay.equals("")) {
							earliestMonth = months.get(monthIndex);
							File monthDirectory = new File(
									path + "/" + mediaType + "/" + camera + "/" + earliestYear + "/" + earliestMonth);
							if (!monthDirectory.isDirectory()) {
								monthIndex++;
							} else {
								List<String> days = Arrays.asList(monthDirectory.list());
								Collections.sort(days);
								if (days.size() > 0) {
									int dayIndex = 0;
									while (dayIndex < days.size()) {
										earliestDay = days.get(dayIndex);
										File dayDirectory = new File(path + "/" + mediaType + "/" + camera + "/"
												+ earliestYear + "/" + earliestMonth + "/" + earliestDay);
										if (!dayDirectory.isDirectory()) {
											dayIndex++;
										} else {
											return earliestYear + "/" + earliestMonth + "/" + earliestDay;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}
