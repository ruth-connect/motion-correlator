package uk.me.ruthmills.motioncorrelator.model.persondetection;

import java.util.List;

import org.opencv.core.Rect;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonDetection {

	private Rect location;
	private List<Double> weights;
	private int imageWidthPixels;

	public PersonDetection(int imageWidthPixels) {
		this.imageWidthPixels = imageWidthPixels;
	}

	public void setLocation(Rect object) {
		this.location = object;
	}

	public Rect getObject() {
		return location;
	}

	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}

	public List<Double> getWeights() {
		return weights;
	}

	@JsonIgnore
	public int getCentreX() {
		return (int) Math.round((convert(location.tl().x) + convert(location.br().x)) / 2d);
	}

	@JsonIgnore
	public int getCentreY() {
		return (int) Math.round((convert(location.tl().y) + convert(location.br().y)) / 2d);
	}

	@JsonIgnore
	public int getLeft() {
		return (int) Math.round(convert(location.tl().x));
	}

	@JsonIgnore
	public int getTop() {
		return (int) Math.round(convert(location.tl().y));
	}

	@JsonIgnore
	public int getRight() {
		return (int) Math.round(convert(location.br().x));
	}

	@JsonIgnore
	public int getBottom() {
		return (int) Math.round(convert(location.br().y));
	}

	@JsonIgnore
	public int getWidth() {
		return (int) Math.round(convert(location.width));
	}

	@JsonIgnore
	public int getHeight() {
		return (int) Math.round(convert(location.height));
	}

	@JsonIgnore
	public double getWeight() {
		return weights.get(0);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("centre x: " + getCentreX());
		stringBuilder.append(", centre y: " + getCentreY());
		stringBuilder.append(", left: " + getLeft());
		stringBuilder.append(", top: " + getTop());
		stringBuilder.append(", right: " + getRight());
		stringBuilder.append(", bottom: " + getBottom());
		stringBuilder.append(", width: " + getWidth());
		stringBuilder.append(", height: " + getHeight());
		stringBuilder.append(", weight: " + getWeight() + "\n");
		return stringBuilder.toString();
	}

	private double convert(double value) {
		return value * 640d / (double) imageWidthPixels;
	}
}
