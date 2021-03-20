package uk.me.ruthmills.motioncorrelator.model;

public class PersonProbability {
	private Detection strongestDetection;
	private int numPersonDetectionFrames;
	private int numVectorDetectionFrames;
	private int numBothDetectionFrames;
	private double cumulativePersonDetectionWeights;

	public PersonProbability(Detection strongestDetection, int numPersonDetectionFrames, int numVectorDetectionFrames,
			int numBothDetectionFrames, double cumulativePersonDetectionWeights) {
		this.strongestDetection = strongestDetection;
		this.numPersonDetectionFrames = numPersonDetectionFrames;
		this.numVectorDetectionFrames = numVectorDetectionFrames;
		this.numBothDetectionFrames = numBothDetectionFrames;
		this.cumulativePersonDetectionWeights = cumulativePersonDetectionWeights;
	}

	public Detection getStrongestDetection() {
		return strongestDetection;
	}

	public int getNumPersonDetectionFrames() {
		return numPersonDetectionFrames;
	}

	public int getNumVectorDetectionFrames() {
		return numVectorDetectionFrames;
	}

	public int getNumBothDetectionFrames() {
		return numBothDetectionFrames;
	}

	public double getCumulativePersonDetectionWeights() {
		return cumulativePersonDetectionWeights;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nPERSON PROBABILITY:");
		stringBuilder.append("\nStrongest Person Detection Weight: ");
		stringBuilder.append(strongestDetection != null ? strongestDetection.getStrongestPersonDetectionWeight() : 0d);
		stringBuilder.append("\nNum Person Detection Frames: ");
		stringBuilder.append(numPersonDetectionFrames);
		stringBuilder.append("\nNum Vector Detection Frames: ");
		stringBuilder.append(numVectorDetectionFrames);
		stringBuilder.append("\nNum Both Detection Frames: ");
		stringBuilder.append(numBothDetectionFrames);
		stringBuilder.append("\nCumulative Person Detection Weights: ");
		stringBuilder.append(cumulativePersonDetectionWeights);
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
}
