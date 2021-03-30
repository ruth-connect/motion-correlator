package uk.me.ruthmills.motioncorrelator.model.vector;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Vector extends VectorData {

	private String region;
	private int x;
	private int y;
	private int dx;
	private int dy;
	private int magnitude;
	private int count;

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegion() {
		return region;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDx() {
		return dx;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public int getDy() {
		return dy;
	}

	public void setMagnitude(int magnitude) {
		this.magnitude = magnitude;
	}

	public int getMagnitude() {
		return magnitude;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	@JsonIgnore
	public int getStartX() {
		return convertX(x, y);
	}

	@JsonIgnore
	public int getStartY() {
		return convertY(x, y);
	}

	@JsonIgnore
	public int getEndX() {
		return convertX(x + dx, y + dy);
	}

	@JsonIgnore
	public int getEndY() {
		return convertY(x + dx, y + dy);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Region: " + region + ", x: " + x + ", y: " + y + ", dx: " + dx + ", dy: " + dy
				+ ", magnitude: " + magnitude + ", count: " + count + "\n");
		stringBuilder.append("ORIGINAL. (" + x + ", " + y + ") -> (" + (x + dx) + ", " + (y + dy) + ")\n");
		stringBuilder.append(
				"CONVERTED. (" + getStartX() + ", " + getStartY() + ") -> (" + getEndX() + ", " + getEndY() + ")\n");
		return stringBuilder.toString();
	}

	private int convertX(int x, int y) {
		return new VectorCoordinates(x, y).convert().getX();
	}

	private int convertY(int x, int y) {
		return new VectorCoordinates(x, y).convert().getY();
	}

	private class VectorCoordinates {
		private int x;
		private int y;

		public VectorCoordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public VectorCoordinates convert() {
			int convertedX = x * 16;
			int convertedY = y * 16;
			return new VectorCoordinates(convertedX, convertedY);
		}
	}
}
