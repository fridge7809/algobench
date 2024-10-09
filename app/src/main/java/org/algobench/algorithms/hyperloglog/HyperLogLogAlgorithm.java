package org.algobench.algorithms.hyperloglog;

import java.util.stream.Stream;

public interface HyperLogLogAlgorithm {
	// unused, refer to main method in implementation
	String calculate(Stream<Integer> input);
}
