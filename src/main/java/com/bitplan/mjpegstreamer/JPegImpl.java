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

/**
 * a JPeg fragment of an MJPeg stream or file
 * 
 * @author wf
 *
 */
public class JPegImpl implements JPeg {
	// we try to keep the dataset small since at 60 fps we might need
	// 216.000 records of this kind per hour at 12 bytes per record
	// this would be 2.5 MBytes per hour
	long offset;
	long length;
	MJPeg mjpeg; // the MJPeg i am belonging to
	byte[] jpegImg;
	int frameIndex;

	@Override
	public long getOffset() {
		return offset;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	@Override
	public int getFrameIndex() {
		return frameIndex;
	}

	/**
	 * a JPeg Image within the file
	 * 
	 * @param frameIndex - the index in the mjpeg video
	 * @param mjpeg      - the MJPeg i am belonging to
	 * @param offset
	 */
	public JPegImpl(MJPeg mjpeg, int frameIndex, long offset) {
		this.mjpeg = mjpeg;
		this.frameIndex = frameIndex;
		this.offset = offset;
	}

	/**
	 * create me from the given frame
	 * 
	 * @param mjpeg      - the MJPeg i am belonging to
	 * @param frameIndex - the index in the mjpeg video
	 * @param offset
	 * @param frame
	 * @throws Exception
	 */
	public JPegImpl(MJPeg mjpeg, int frameIndex, long offset, byte[] frame) throws Exception {
		this(mjpeg, frameIndex, offset);
		this.length = frame.length;
		jpegImg = frame;
	}

	@Override
	public MJPeg getMJPeg() {
		return mjpeg;
	}

	@Override
	public byte[] getImage() {
		// TODO Auto-generated method stub
		return jpegImg;
	}
}