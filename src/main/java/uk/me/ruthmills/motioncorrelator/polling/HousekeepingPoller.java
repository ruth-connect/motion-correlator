package uk.me.ruthmills.motioncorrelator.polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.service.HousekeepingService;

@Component
public class HousekeepingPoller {

	@Autowired
	private HousekeepingService housekeepingService;

	private final Logger logger = LoggerFactory.getLogger(HousekeepingPoller.class);

	@Scheduled(cron = "0 */1 * * * *")
	public void tick() {
		try {
			housekeepingService.runHousekeeping();
		} catch (Exception ex) {
			logger.error("Exception in Housekeeping poller thread", ex);
		}
	}
}
