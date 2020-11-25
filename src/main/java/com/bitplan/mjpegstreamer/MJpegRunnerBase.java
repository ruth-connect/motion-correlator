/**
 * Copyright (c) 2013-2020 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.mjpegstreamer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.mjpegstreamer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.ruthmills.motioncorrelator.model.image.Image;

/**
 * base class for MJPegRunners
 * 
 * @author wf
 * 
 */
public abstract class MJpegRunnerBase implements MJpegReaderRunner {
	private final Logger logger = LoggerFactory.getLogger(MJpegReaderRunner.class);

	protected MJpegRenderer viewer;
	private String urlString, user, pass;
	protected boolean frameAvailable = false;
	protected BufferedInputStream inputStream;

	private URL url;
	protected byte[] curFrame;
	// count each frame
	private int framesReadCount;
	protected long bytesRead = 0;

	protected int framesRenderedCount;
	// count frames in last second for frame per second calculation
	// nano time of last frame
	private long fpsFrameNanoTime;
	// nano time of the first frame
	private long firstFrameNanoTime;
	// nano time of last second
	private long fpssecond;
	private Thread streamReader;
	protected URLConnection conn;

	// how many milliseconds to wait for next frame to limit fps
	private int fpsLimitMillis = 0;
	protected boolean connected = false;

	private long now;

	private StopWatch stopWatch;

	public MJpegRunnerBase() {
	}

	/**
	 * create a MJpegRunner
	 * 
	 * @param urlString
	 * @param user
	 * @param pass
	 * @throws IOException
	 */
	@Override
	public void init(String urlString, String user, String pass) throws IOException {
		this.urlString = urlString;
		this.user = user;
		this.pass = pass;
		url = new URL(urlString);
		init(url.openStream());
	}

	/**
	 * @return the viewer
	 */
	public MJpegRenderer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer the viewer to set
	 */
	public void setViewer(MJpegRenderer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return the urlString
	 */
	public String getUrlString() {
		return urlString;
	}

	@Override
	public int getFramesRead() {
		return this.framesReadCount;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}

	// input buffers size (14 msecs at 568 x 768)
	public static int INPUT_BUFFER_SIZE = 8192 * 2;

	/**
	 * limit the number of frames per second
	 * 
	 * @param fpsLimit e.g. 10 for one frame each 100 millisecs, 0.5 for one frame
	 *                 each 2000 millisecs
	 */
	public void setFPSLimit(double fpsLimit) {
		fpsLimitMillis = (int) (1000 / fpsLimit);
	}

	/**
	 * get a Base64 Encoder
	 * 
	 * @return the base 64 encoder
	 */
	public Base64 getEncoder() {
		// JDK 8
		// Base64.Encoder base64 = Base64.getEncoder();
		// Apache Commons codec
		Base64 base64 = new Base64();
		return base64;
	}

	/**
	 * open the connection
	 * 
	 * @return the connection stream
	 */
	public BufferedInputStream openConnection() {
		BufferedInputStream result = null;
		try {
			logger.info("Connecting to: " + urlString);
			url = new URL(urlString);
			conn = url.openConnection();
			if (user != null) {
				String credentials = user + ":" + pass;
				Base64 base64 = getEncoder();
				byte[] encoded_credentials = base64.encode(credentials.getBytes());
				String authStringEnc = new String(encoded_credentials);
				// System.out.println("Base64 encoded auth string: " + authStringEnc);
				conn.setRequestProperty("Authorization", "Basic " + authStringEnc);
			}
			// change the timeout to taste, I like 1 second
			conn.setReadTimeout(5000); // 5 seconds
			conn.connect();
			result = new BufferedInputStream(conn.getInputStream(), INPUT_BUFFER_SIZE);
		} catch (MalformedURLException e) {
			logger.error("Invalid URL", e);
		} catch (IOException ioe) {
			logger.error("Unable to connect: ", ioe);
		}
		return result;
	}

	/**
	 * connect
	 */
	public void connect() {
		connected = true;
		// if inputStream has been set - keep it!
		if (inputStream == null) {
			if ("-".equals(urlString))
				inputStream = new BufferedInputStream(System.in, INPUT_BUFFER_SIZE);
			else
				inputStream = openConnection();
		}
	}

	/**
	 * start reading
	 */
	public synchronized void start() {
		framesReadCount = 0;
		framesRenderedCount = 0;
		viewer.init();
		this.streamReader = new Thread(this, "Stream reader");
		stopWatch = new StopWatch();
		stopWatch.start();
		streamReader.start();
	}

	/**
	 * wait until I am finished
	 */
	public void join() throws InterruptedException {
		if (streamReader != null) {
			streamReader.join();
		}
	}

	/**
	 * is there a new frame?
	 * 
	 * @return if a new frame is available
	 */
	public boolean isAvailable() {
		return frameAvailable;
	}

	/**
	 * get the total elapsedTime
	 * 
	 * @return the total elapsed time in milliseconds
	 */
	public long elapsedTimeMillisecs() {
		long elapsed = this.now - this.firstFrameNanoTime;
		long result = TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS);
		return result;
	}

	/**
	 * get a debug message with current time
	 * 
	 * @return a debug message containing the number of frames read with the elapsed
	 *         time
	 */
	public String getTimeMsg(String msg) {
		String streamName = "?";
		if (inputStream != null)
			streamName = inputStream.getClass().getSimpleName();
		String timeMsg = streamName + " at frame " + framesReadCount + "->" + framesRenderedCount + msg + " total="
				+ this.elapsedTimeMillisecs() + " msecs " + this;
		return timeMsg;
	}

	/**
	 * get a time debugging message
	 * 
	 * @return a debug message with the given time and no title
	 */
	public String getTimeMsg() {
		return getTimeMsg("");
	}

	/**
	 * read
	 */
	public void read() {
		try {
			if (framesReadCount == 0) {
				this.firstFrameNanoTime = System.nanoTime();
				this.fpsFrameNanoTime = firstFrameNanoTime;
				this.fpssecond = fpsFrameNanoTime;
				this.bytesRead = 0;
			}
			Image image = new Image();
			image.setTimestamp(LocalDateTime.now());
			image.setBytes(curFrame);
			bytesRead += curFrame.length;
			viewer.renderNextImage(image);

			frameAvailable = false;

			// uncomment next line for debug image
			// image= viewer.getBufferedImage("/images/start.png");
			// viewer.repaint();
			// Frame per second calculation
			now = System.nanoTime();
			// how many nanosecs since last frame?
			long elapsedFrameTime = now - fpsFrameNanoTime;
			// how many nanosecs since last second timestamp
			long elapsedSecondTime = now - fpssecond;
			long framemillisecs = TimeUnit.MILLISECONDS.convert(elapsedFrameTime, TimeUnit.NANOSECONDS);
			long secmillisecs = TimeUnit.MILLISECONDS.convert(elapsedSecondTime, TimeUnit.NANOSECONDS);
			// is a second over?
			if (secmillisecs > 1000) {
				fpssecond = now;
			}
			// do not render images that are "too quick/too early"
			if (framemillisecs >= this.fpsLimitMillis) {
				// how many frames we actually displayed
				framesRenderedCount++;
				fpsFrameNanoTime = now;
			}
		} catch (Throwable th) {
			logger.error("Error acquiring the frame: ", th);
		}
	}

	/**
	 * when disposing stop
	 */
	public void dispose() {
		stop("disposing " + this);
	}

	/**
	 * Stop the loop, and allow it to clean up
	 */
	public synchronized void stop(String msg) {
		connected = false;
		if (viewer != null)
			viewer.stop(msg);
	}
}
