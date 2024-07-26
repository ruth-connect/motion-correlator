package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
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

	private double mediaPercentFree = Double.NaN;

	private double remotePercentFree = Double.NaN;

	@Autowired
	private HomeAssistantService homeAssistantService;

	private Housekeeping housekeeping;

	private static final Logger logger = LoggerFactory.getLogger(HousekeepingServiceImpl.class);

	@PostConstruct
	public void initialise() {
		housekeeping = new Housekeeping();
		housekeeping.initialise();
	}

	@Override
	public void reportDiskUsage() {
		if (mediaPercentFree != Double.NaN) {
			BigDecimal mediaPercentUsed = new BigDecimal(100d - getPercentageDiskSpaceFree(mediaPath));
			homeAssistantService
					.notifyMediaDiskSpaceUsed(mediaPercentUsed.setScale(1, RoundingMode.DOWN).toPlainString());
		}
		if (remotePercentFree == Double.NaN) {
			homeAssistantService.notifyRemoteDiskSpaceUsed("unavailable");
		} else {
			BigDecimal remotePercentUsed = new BigDecimal(100d - getPercentageDiskSpaceFree(remotePath));
			homeAssistantService
					.notifyRemoteDiskSpaceUsed(remotePercentUsed.setScale(1, RoundingMode.DOWN).toPlainString());
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

	private class Housekeeping implements Runnable {

		private Thread housekeepingThread;

		public void initialise() {
			housekeepingThread = new Thread(this, "Housekeeping");
			housekeepingThread.setPriority(3); // lower priority than normal (5).
			housekeepingThread.start();
			logger.info("Housekeeping Thread started");
		}

		@Override
		public void run() {
			while (true) {
				try {
					runHousekeeping();
				} catch (Exception ex) {
					logger.error("Failed to run housekeeping", ex);
				}
				try {
					Thread.sleep(600000);
				} catch (Exception ex) {
					logger.error("Interrupted", ex);
				}
			}
		}

		public void runHousekeeping() {
			logger.info("Running Housekeeping...");
			mediaPercentFree = getPercentageDiskSpaceFree(mediaPath);
			remotePercentFree = getPercentageDiskSpaceFree(remotePath);
			if (mediaPercentFree < mediaMinPercentFree) {
				logger.info("Media percent free is below minimum of " + mediaMinPercentFree + "%");
				freeDiskSpace(false, mediaPath, mediaPercentFree, mediaMinPercentFree);
			}
			if (remotePercentFree == Double.NaN) {
				logger.warn("Remote percent free is unavailable - remote disk may be offline");
			} else if (remotePercentFree < remoteMinPercentFree) {
				logger.info("Remote percent free is below minimum of " + remoteMinPercentFree + "%");
				freeDiskSpace(true, remotePath, remotePercentFree, remoteMinPercentFree);
			}
			logger.info("Housekeeping Complete!");
		}

		private void freeDiskSpace(boolean isRemote, String path, double percentFree, double minPercentFree) {
			if (isRemote) {
				homeAssistantService.notifyRemoteDiskSpaceFreeStart();
			} else {
				homeAssistantService.notifyDiskSpaceFreeStart();
			}

			do {
				double oldPercentFree = percentFree;
				logger.info("Freeing disk space for: " + path);
				String earliestDay = getEarliestDay(path);
				logger.info("Earliest day to free disk space for: " + earliestDay);
				freeDiskSpaceForDay(path, earliestDay);
				percentFree = getPercentageDiskSpaceFree(path);
				logger.info("Disk space free for " + path + " now: " + percentFree);

				if (oldPercentFree == percentFree) {
					logger.error("Error! No disk space was freed! Exiting loop.");
					if (isRemote) {
						homeAssistantService.notifyRemoteDiskSpaceNotFreed();
					} else {
						homeAssistantService.notifyDiskSpaceNotFreed();
					}
					return; // exit the method.
				}
			} while (percentFree < minPercentFree);

			if (isRemote) {
				homeAssistantService.notifyRemoteDiskSpaceFreed();
			} else {
				homeAssistantService.notifyDiskSpaceFreed();
			}
		}

		private void freeDiskSpaceForDay(String path, String day) {
			logger.info("freeDiskSpaceForDay invoked. Path: " + path + ", day: " + day);
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
								logger.info("Adding earliest day to list: " + earliestDay);
								earliestDays.add(earliestDay);
							}
						}
					}
				}
			}
			if (earliestDays.size() == 0) {
				logger.warn("Earliest days list size is zero! Returning null");
				return null;
			} else {
				Collections.sort(earliestDays);
				logger.info("Earliest day is: " + earliestDays.get(0));
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
								File monthDirectory = new File(path + "/" + mediaType + "/" + camera + "/"
										+ earliestYear + "/" + earliestMonth);
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
												logger.info("Returning earliest day for camera " + camera + ": "
														+ earliestYear + "/" + earliestMonth + "/" + earliestDay);
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
			logger.warn("No earliest day for camera: " + camera + " found! Returning null");
			return null;
		}
	}
}
