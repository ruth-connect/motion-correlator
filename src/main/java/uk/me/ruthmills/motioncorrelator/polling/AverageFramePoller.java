package uk.me.ruthmills.motioncorrelator.polling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import uk.me.ruthmills.motioncorrelator.service.AverageFrameService;

@Component
public class AverageFramePoller {

	@Autowired
	private AverageFrameService averageFrameService;

	private final Logger logger = LoggerFactory.getLogger(AverageFramePoller.class);

	@Scheduled(cron = "*/1 * * * * *")
	public void tick() {
		try {
			averageFrameService.addCurrentFrame("hal9000");
			averageFrameService.addCurrentFrame("themekon");
			averageFrameService.addCurrentFrame("bigbrother");
		} catch (Exception ex) {
			logger.error("Exception in poller thread", ex);
		}
	}
}
