package org.algobench.algorithms.foursum;

import org.algobench.app.factory.AlgorithmContext;

public record FourSumContext(
		FourSumAlgorithm algorithm) implements AlgorithmContext {

	public int[] calculate(int[] nums) {
		return algorithm.calculate(nums);
	}

	@Override
	public int[][] calculateTwoDimensional(int[][] input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FourSumAlgorithm algorithm() {
		return algorithm;
	}
}
