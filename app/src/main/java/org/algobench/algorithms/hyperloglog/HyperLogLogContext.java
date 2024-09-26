package org.algobench.algorithms.hyperloglog;

import org.algobench.app.factory.AlgorithmContext;

import java.util.stream.Stream;

public record HyperLogLogContext(HyperLogLogAlgorithm hyperLogLog) implements AlgorithmContext {
	@Override
	public int[] calculate(int[] input) {
		return new int[0];
	}

	@Override
	public int[][] calculateTwoDimensional(int[][] input) {
		return new int[0][];
	}

	@Override
	public String calculateStream(Stream<Integer> input) {
		return hyperLogLog.calculate(input);
	}
}
