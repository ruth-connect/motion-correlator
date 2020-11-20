package uk.me.ruthmills.motioncorrelator.model.vector;

import org.apache.commons.lang3.builder.ToStringBuilder;

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

	public String toString() {
		return new ToStringBuilder(this).append("region", region).append("x", x).append("y", y).append("dx", dx)
				.append("dy", dy).append("magnitude", magnitude).append("count", count).toString();
	}
}
