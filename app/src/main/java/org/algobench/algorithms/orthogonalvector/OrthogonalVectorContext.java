package org.algobench.algorithms.orthogonalvector;

import org.algobench.app.factory.AlgorithmContext;

import java.util.stream.Stream;

public record OrthogonalVectorContext(OrthogonalVectorAlgorithm algorithm) implements AlgorithmContext {
	@Override
	public int[] calculate(int[] input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int[][] calculateTwoDimensional(int[][] input) {
		return algorithm.calculate(input);
	}

	@Override
	public String calculateStream(Stream<Integer> input) {
		return "";
	}
}
