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

	@Value("${endpoint}")
	private String endpoint;

	@Value("${token}")
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
		homeAssistantNotifier.notify(camera.getLocation() + "_" + "camera_connected", LocalDateTime.now().toString());
	}

	@Override
	public void notifyCameraConnectionFailed(Camera camera) {
		logger.info(camera.getName() + " connection failed");
		homeAssistantNotifier.notify(camera.getLocation() + "_" + "camera_connection_failed",
				LocalDateTime.now().toString());
	}

	@Override
	public void notifyPersonDetected(Camera camera, PersonDetections personDetections) {
		logger.info(camera.getName() + " person detected");
		homeAssistantNotifier.notify(camera.getLocation() + "_" + "camera_person_detected",
				ImageUtils.getImagePath("http://heimdallr/images/" + camera.getName(), personDetections.getImage())
						+ personDetections.getDetectionsFilename());
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

		public void notify(String location, String type) {
			messages.offer(new HomeAssistantMessage(location, type));
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
			logger.info("JSON to send: " + requestJson);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + token);

			String url = endpoint + "sensor." + message.getSensorName();

			logger.info("About to send POST to " + url);
			restTemplate.postForEntity(url, new HttpEntity<String>(requestJson.toString(), headers), String.class);
		}
	}
}
