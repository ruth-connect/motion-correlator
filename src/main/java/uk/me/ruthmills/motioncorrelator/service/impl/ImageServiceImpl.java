package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import uk.me.ruthmills.motioncorrelator.model.image.Image;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetection;
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

	private static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("/yyyy/MM/dd/HH");

	@Override
	public Image readImage(String camera) throws IOException, URISyntaxException {
		URI uri = new URI("http://" + camera + "/mjpeg_read.php");
		ClientHttpRequest request = getClientHttpRequestFactory().createRequest(uri, HttpMethod.GET);
		LocalDateTime timestamp = LocalDateTime.now();
		ClientHttpResponse response = request.execute();
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new IOException("Failed to read image. HTTP status code: " + response.getStatusText());
		}
		InputStream body = response.getBody();
		byte[] bytes = IOUtils.toByteArray(body);
		response.close();
		Image image = new Image();
		image.setTimestamp(timestamp);
		image.setBytes(bytes);
		return image;
	}

	public void writeImage(String camera, Image image, PersonDetections personDetections, boolean stamped)
			throws IOException {
		LocalDateTime timestamp = image.getTimestamp();
		String path = "/mnt/media/motioncorrelator/" + camera + timestamp.format(DATE_TIME_FORMAT);
		File file = new File(path);
		file.mkdir();
		List<PersonDetection> detectionsList = personDetections.getPersonDetections();
		String detections = detectionsList.size() > 0 ? "-" + detectionsList.size() + "-"
				+ new BigDecimal(detectionsList.get(0).getWeight()).setScale(3, RoundingMode.HALF_UP) : "";
		String filename = timestamp + (stamped ? ("-stamped" + detections) : "") + ".jpg";
		Files.write(FileSystems.getDefault().getPath(path, filename), image.getBytes(), StandardOpenOption.CREATE);
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		int timeout = 9000;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return new HttpComponentsClientHttpRequestFactory(client);
	}
}
