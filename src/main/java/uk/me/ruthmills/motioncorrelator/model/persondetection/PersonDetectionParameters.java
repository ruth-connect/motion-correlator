package uk.me.ruthmills.motioncorrelator.model.persondetection;

public class PersonDetectionParameters {

	private double hitThreshold;
	private int winStrideX;
	private int winStrideY;
	private int paddingX;
	private int paddingY;
	private double scale;
	private double finalThreshold;

	public PersonDetectionParameters() {
	}

	public PersonDetectionParameters(double hitThreshold, int winStrideX, int winStrideY, int paddingX, int paddingY,
			double scale, double finalThreshold) {
		this.hitThreshold = hitThreshold;
		this.winStrideX = winStrideX;
		this.winStrideY = winStrideY;
		this.paddingX = paddingX;
		this.paddingY = paddingY;
		this.scale = scale;
		this.finalThreshold = finalThreshold;
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

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("hitThreshold: " + hitThreshold);
		stringBuilder.append(", winStrideX: " + winStrideX);
		stringBuilder.append(", winStrideY: " + winStrideY);
		stringBuilder.append(", paddingX: " + paddingX);
		stringBuilder.append(", paddingY: " + paddingY);
		stringBuilder.append(", scale: " + scale);
		stringBuilder.append(", finalThreshold: " + finalThreshold);
		return stringBuilder.toString();
	}

	public double getFinalThreshold() {
		return finalThreshold;
	}

	public void setFinalThreshold(double finalThreshold) {
		this.finalThreshold = finalThreshold;
	}
}
