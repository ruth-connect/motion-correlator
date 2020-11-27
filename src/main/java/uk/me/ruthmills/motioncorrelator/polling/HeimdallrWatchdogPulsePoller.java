package uk.me.ruthmills.motioncorrelator.polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;

@Component
public class HeimdallrWatchdogPulsePoller {

	@Autowired
	private HomeAssistantService homeAssistantService;

	private final Logger logger = LoggerFactory.getLogger(HeimdallrWatchdogPulsePoller.class);

	@Scheduled(cron = "*/5 * * * * *")
	public void tick() {
		try {
			homeAssistantService.notifyHeimdallrWatchdogPulse();
		} catch (Exception ex) {
			logger.error("Exception in Heimdallr Watchdog Pulse poller thread", ex);
		}
	}
}
