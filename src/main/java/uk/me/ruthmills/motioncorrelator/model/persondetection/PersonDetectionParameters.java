package uk.me.ruthmills.motioncorrelator.model.persondetection;

public class PersonDetectionParameters {

	private int imageWidthPixels;
	private double hitThreshold;
	private int winStrideX;
	private int winStrideY;
	private int paddingX;
	private int paddingY;
	private double scale;
	private double frameDeltaThreshold;

	public PersonDetectionParameters() {
		imageWidthPixels = 320;
		hitThreshold = 0d;
		winStrideX = 4;
		winStrideY = 4;
		paddingX = 8;
		paddingY = 8;
		scale = 1.09d;
		frameDeltaThreshold = 4d;
	}

	public PersonDetectionParameters(int imageWidthPixels, double hitThreshold, int winStrideX, int winStrideY,
			int paddingX, int paddingY, double scale) {
		this.imageWidthPixels = imageWidthPixels;
		this.hitThreshold = hitThreshold;
		this.winStrideX = winStrideX;
		this.winStrideY = winStrideY;
		this.paddingX = paddingX;
		this.paddingY = paddingY;
		this.scale = scale;
	}

	public int getImageWidthPixels() {
		return imageWidthPixels;
	}

	public void setImageWidthPixels(int imageWidthPixels) {
		this.imageWidthPixels = imageWidthPixels;
	}

	public double getHitThreshold() {
		return hitThreshold;
	}

	public void setHitThreshold(double hitThreshold) {
		this.hitThreshold = hitThreshold;
	}

	public int getWinStrideX() {
		return winStrideX;
	}

	public void setWinStrideX(int winStrideX) {
		this.winStrideX = winStrideX;
	}

	public int getWinStrideY() {
		return winStrideY;
	}

	public void setWinStrideY(int winStrideY) {
		this.winStrideY = winStrideY;
	}

	public int getPaddingX() {
		return paddingX;
	}

	public void setPaddingX(int paddingX) {
		this.paddingX = paddingX;
	}

	public int getPaddingY() {
		return paddingY;
	}

	public void setPaddingY(int paddingY) {
		this.paddingY = paddingY;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getFrameDeltaThreshold() {
		return frameDeltaThreshold;
	}

	public void setFrameDeltaThreshold(double frameDeltaThreshold) {
		this.frameDeltaThreshold = frameDeltaThreshold;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(", imageWidthPixels: " + imageWidthPixels);
		stringBuilder.append(", hitThreshold: " + hitThreshold);
		stringBuilder.append(", winStrideX: " + winStrideX);
		stringBuilder.append(", winStrideY: " + winStrideY);
		stringBuilder.append(", paddingX: " + paddingX);
		stringBuilder.append(", paddingY: " + paddingY);
		stringBuilder.append(", scale: " + scale);
		return stringBuilder.toString();
	}
}
