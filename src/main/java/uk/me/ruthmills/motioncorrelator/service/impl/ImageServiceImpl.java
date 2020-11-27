package uk.me.ruthmills.motioncorrelator.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

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
import uk.me.ruthmills.motioncorrelator.model.persondetection.PersonDetections;
import uk.me.ruthmills.motioncorrelator.service.ImageService;
import uk.me.ruthmills.motioncorrelator.util.ImageUtils;

@Service
public class ImageServiceImpl implements ImageService {

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
		return new Image(timestamp, bytes);
	}

	@Override
	public void writeImage(String camera, Image image, PersonDetections personDetections, boolean stamped)
			throws IOException {
		String path = ImageUtils.getImagePath(camera, image);
		File file = new File(path);
		file.mkdirs();
		String filename = personDetections.getDetectionsFilename();
		Files.write(FileSystems.getDefault().getPath(path, filename), image.getBytes(), StandardOpenOption.CREATE);
	}

	@Override
	public void writeImage(String camera, Image image, String suffix) throws IOException {
		if (image != null) {
			LocalDateTime timestamp = image.getTimestamp();
			String path = ImageUtils.getImagePath(camera, image);
			File file = new File(path);
			file.mkdirs();
			String filename = timestamp + suffix + ".jpg";
			Files.write(FileSystems.getDefault().getPath(path, filename), image.getBytes(), StandardOpenOption.CREATE);
		}
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		int timeout = 9000;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
				.setSocketTimeout(timeout).build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return new HttpComponentsClientHttpRequestFactory(client);
	}
}
