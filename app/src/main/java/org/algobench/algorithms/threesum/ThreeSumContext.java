package org.algobench.algorithms.threesum;

import org.algobench.app.factory.AlgorithmContext;

import java.util.stream.Stream;

public record ThreeSumContext(
		ThreeSumAlgorithm algorithm) implements AlgorithmContext {

	public int[] calculate(int[] nums) {
		return algorithm.calculate(nums);
	}

	@Override
	public int[][] calculateTwoDimensional(int[][] input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String calculateStream(Stream<Integer> input) {
		return "";
	}
}
