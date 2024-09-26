package org.algobench.algorithms.hyperloglog;

import java.util.stream.Stream;

public interface HyperLogLogAlgorithm {
	String calculate(Stream<Integer> input);
}
