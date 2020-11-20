package uk.me.ruthmills.motioncorrelator.model.vector;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Burst extends VectorData {

	private int burstCount;

	public void setBurstCount(int burstCount) {
		this.burstCount = burstCount;
	}

	public int getBurstCount() {
		return burstCount;
	}

	public String toString() {
		return new ToStringBuilder(this).append("burstCount", burstCount).toString();
	}
}
