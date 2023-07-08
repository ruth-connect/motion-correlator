package uk.me.ruthmills.motioncorrelator.model;

/**
 * Latency.
 * 
 * @author ruth
 */
public class Latency {

	private Integer minLatency;

	private Integer maxLatency;

	private int count;

	private long sum;

	/**
	 * Default constructor.
	 */
	public Latency() {
	}

	/**
	 * Copy constructor.
	 */
	public Latency(Latency latency) {
		this.minLatency = latency.minLatency;
		this.maxLatency = latency.maxLatency;
		this.count = latency.count;
		this.sum = latency.sum;
	}

	/**
	 * Process the latency.
	 */
	public void processLatency(int latency) {
		synchronized (this) {
			if (minLatency == null || latency < minLatency) {
				minLatency = latency;
			}
			if (maxLatency == null || latency < maxLatency) {
				maxLatency = latency;
			}
			count++;
			sum += latency;
		}
	}

	public Latency getLatency() {
		synchronized (this) {
			return new Latency(this);
		}
	}

	public int getMinLatency() {
		return minLatency;
	}

	public int getMaxLatency() {
		return maxLatency;
	}

	public int getAverageLatency() {
		return (int) (sum / count);
	}

	public String toString() {
		return "{\"minLatency\":" + getMinLatency() + ",\"maxLatency\":" + getMaxLatency() + ",\"averageLatency\":"
				+ getAverageLatency() + "}";
	}
}
