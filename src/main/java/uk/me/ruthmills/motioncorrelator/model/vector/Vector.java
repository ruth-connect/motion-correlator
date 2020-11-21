package uk.me.ruthmills.motioncorrelator.model.vector;

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

	public int convertX(int x) {
		return Math.round((float) x * 640f / 100f);
	}

	public int convertY(int y) {
		return Math.round((float) y * 480f / 100f);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ORIGINAL. region: " + region + ", x: " + x + ", y: " + y + ", dx: " + dx + ", dy: " + dy
				+ ", magnitude: " + magnitude + ", count: " + count + "\n");
		stringBuilder.append("CONVERTED. region: " + region + ", x: " + convertX(x) + ", y: " + convertY(y) + ", dx: "
				+ convertX(dx) + ", dy: " + convertY(dy) + ", magnitude: " + magnitude + ", count: " + count + "\n");
		return stringBuilder.toString();
	}
}
