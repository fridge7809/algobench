package org.algobench.app.factory;

import java.util.stream.Stream;

public interface AlgorithmContext {
	int[] calculate(int[] input);
	int[][] calculateTwoDimensional(int[][] input);
	String calculateStream(Stream<Integer> input);
}
