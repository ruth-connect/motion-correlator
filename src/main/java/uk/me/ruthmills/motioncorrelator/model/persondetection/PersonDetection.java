package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.util.List;

import org.opencv.core.Rect;

public class PersonDetection {

	private int centreX;
	private int centreY;
	private int left;
	private int top;
	private int right;
	private int bottom;
	private int width;
	private int height;

	private double weight;

	public PersonDetection() {
	}

	public PersonDetection(int imageWidthPixels, Rect location, List<Double> weights) {
		centreX = (int) Math
				.round((convert(location.tl().x, imageWidthPixels) + convert(location.br().x, imageWidthPixels)) / 2d);
		centreY = (int) Math
				.round((convert(location.tl().y, imageWidthPixels) + convert(location.br().y, imageWidthPixels)) / 2d);
		left = (int) Math.round(convert(location.tl().x, imageWidthPixels));
		top = (int) Math.round(convert(location.tl().y, imageWidthPixels));
		right = (int) Math.round(convert(location.br().x, imageWidthPixels));
		bottom = (int) Math.round(convert(location.br().y, imageWidthPixels));
		width = (int) Math.round(convert(location.width, imageWidthPixels));
		height = (int) Math.round(convert(location.height, imageWidthPixels));

		weight = weights.get(0);
	}

	public int getCentreX() {
		return centreX;
	}

	public void setCentreX(int centreX) {
		this.centreX = centreX;
	}

	public int getCentreY() {
		return centreY;
	}

	public void setCentreY(int centreY) {
		this.centreY = centreY;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("centre x: " + centreX);
		stringBuilder.append(", centre y: " + centreY);
		stringBuilder.append(", left: " + left);
		stringBuilder.append(", top: " + top);
		stringBuilder.append(", right: " + right);
		stringBuilder.append(", bottom: " + bottom);
		stringBuilder.append(", width: " + width);
		stringBuilder.append(", height: " + height);
		stringBuilder.append(", weight: " + weight + "\n");
		return stringBuilder.toString();
	}

	private double convert(double value, int imageWidthPixels) {
		return value * 640d / (double) imageWidthPixels;
	}
}
