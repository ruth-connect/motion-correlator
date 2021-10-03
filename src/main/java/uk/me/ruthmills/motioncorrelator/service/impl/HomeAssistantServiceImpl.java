package uk.me.ruthmills.motioncorrelator.service.impl;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import uk.me.ruthmills.motioncorrelator.model.Camera;
import uk.me.ruthmills.motioncorrelator.model.HomeAssistantMessage;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.HomeAssistantService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class HomeAssistantServiceImpl implements HomeAssistantService {

	@Value("${homeassistant.endpoint}")
	private String endpoint;

	@Value("${homeassistant.token}")
	private String token;

	private HomeAssistantNotifier homeAssistantNotifier = new HomeAssistantNotifier();
	private RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(HomeAssistantServiceImpl.class);

	@PostConstruct
	public void initialise() {
		restTemplate = new RestTemplate(getClientHttpRequestFactory());
		homeAssistantNotifier.initialise();
		notifyHeimdallrWatchdogPulse();
	}

	@Override
	public void notifyHeimdallrWatchdogPulse() {
		homeAssistantNotifier.notify("heimdallr_watchdog_pulse", LocalDateTime.now().toString());
	}

	@Override
	public void notifyCameraConnected(Camera camera) {
		logger.info(camera.getName() + " connected");
		homeAssistantNotifier.notify(camera.getName() + "_" + "camera_connected", LocalDateTime.now().toString());
	}

	@Override
	public void notifyCameraConnectionFailed(Camera camera) {
		logger.info(camera.getName() + " connection failed");
		homeAssistantNotifier.notify(camera.getName() + "_" + "camera_connection_failed",
				LocalDateTime.now().toString());
	}

	public void notifyCameraStreamBehindSchedule(Camera camera) {
		logger.info(camera.getName() + " stream behind schedule");
		homeAssistantNotifier.notify(camera.getName() + "_" + "camera_stream_behind_schedule",
				LocalDateTime.now().toString());
	}

	@Override
	public void notifyDiskWriteOK() {
		logger.info("Disk Write OK");
		homeAssistantNotifier.notify("heimdallr_disk_write_ok", LocalDateTime.now().toString());
	}

	@Override
	public void notifyDiskWriteFailed() {
		logger.info("Disk Write Failed");
		homeAssistantNotifier.notify("heimdallr_disk_write_failed", LocalDateTime.now().toString());
	}

	@Override
	public void notifyDiskSpaceFreeStart() {
		logger.info("Disk Space Free Start");
		homeAssistantNotifier.notify("heimdallr_disk_space_free_start", LocalDateTime.now().toString());
	}

	@Override
	public void notifyDiskSpaceFreed() {
		logger.info("Disk Space Freed");
		homeAssistantNotifier.notify("heimdallr_disk_space_freed", LocalDateTime.now().toString());
	}

	@Override
	public void notifyDiskSpaceNotFreed() {
		logger.info("Disk Space Not Freed");
		homeAssistantNotifier.notify("heimdallr_disk_space_not_freed", LocalDateTime.now().toString());
	}

	@Override
	public void notifyRemoteDiskWriteOK() {
		logger.info("Remote Disk Write OK");
		homeAssistantNotifier.notify("heimdallr_remote_disk_write_ok", LocalDateTime.now().toString());
	}

	@Override
	public void notifyRemoteDiskWriteFailed() {
		logger.info("Remote Disk Write Failed");
		homeAssistantNotifier.notify("heimdallr_remote_disk_write_failed", LocalDateTime.now().toString());
	}

	@Override
	public void notifyRemoteDiskSpaceFreeStart() {
		logger.info("Remote Disk Space Free Start");
		homeAssistantNotifier.notify("heimdallr_remote_disk_space_free_start", LocalDateTime.now().toString());
	}

	@Override
	public void notifyRemoteDiskSpaceFreed() {
		logger.info("Remote Disk Space Freed");
		homeAssistantNotifier.notify("heimdallr_remote_disk_space_freed", LocalDateTime.now().toString());
	}

	@Override
	public void notifyRemoteDiskSpaceNotFreed() {
		logger.info("Remote Disk Space Not Freed");
		homeAssistantNotifier.notify("heimdallr_remote_disk_space_not_freed", LocalDateTime.now().toString());
	}

	@Override
	public void notifyMediaDiskSpaceUsed(String mediaDiskSpace) {
		logger.info("Media Disk Space Used: " + mediaDiskSpace + "%");
		homeAssistantNotifier.notify("heimdallr_media_disk_space_used", mediaDiskSpace);
	}

	@Override
	public void notifyRemoteDiskSpaceUsed(String remoteDiskSpace) {
		logger.info("Remote Disk Space Used: " + remoteDiskSpace + "%");
		homeAssistantNotifier.notify("heimdallr_remote_disk_space_used", remoteDiskSpace);
	}

	@Override
	public void notifyPersonDetected(Camera camera, long sequence, LocalDateTime timestamp,
			PersonDetections personDetections) {
		logger.info(camera.getName() + " person detected");
		homeAssistantNotifier.notify(camera.getName() + "_" + "camera_person_detected", "stamped/"
				+ ImageUtils.getImagePath(camera.getName(), timestamp) + timestamp + "-" + sequence + ".jpg");
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		int timeout = 9000;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return new HttpComponentsClientHttpRequestFactory(client);
	}

	private class HomeAssistantNotifier implements Runnable {

		private BlockingQueue<HomeAssistantMessage> messages = new LinkedBlockingDeque<>();
		private Thread notifier;

		public void initialise() {
			this.notifier = new Thread(this, "Home Assistant notifier");
			notifier.start();
			logger.info("Started Home Assistant notifier thread");
		}

		public void notify(String name, String type) {
			messages.offer(new HomeAssistantMessage(name, type));
		}

		@Override
		public void run() {
			while (true) {
				try {
					HomeAssistantMessage message = messages.take();
					sendMessage(message);
				} catch (Exception ex) {
					logger.error("Failed to notify Home Assistant", ex);
				}
			}
		}

		private void sendMessage(HomeAssistantMessage message) {
			StringBuilder requestJson = new StringBuilder();
			requestJson.append("{\"state\": \"");
			requestJson.append(message.getSensorValue());
			requestJson.append("\"}");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + token);

			String url = endpoint + "sensor." + message.getSensorName();

			restTemplate.postForEntity(url, new HttpEntity<String>(requestJson.toString(), headers), String.class);
		}
	}
}
