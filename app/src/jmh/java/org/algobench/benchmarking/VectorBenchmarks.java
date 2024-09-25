package org.algobench.benchmarking;

import org.algobench.algorithms.orthogonalvector.OrthogonalVectorAlgorithm;
import org.algobench.algorithms.threesum.ThreeSumCubic;
import org.algobench.algorithms.threesum.ThreeSumHashmap;
import org.algobench.algorithms.threesum.ThreeSumQuadratic;
import org.junit.jupiter.api.parallel.Execution;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class VectorBenchmarks {

	@Benchmark
	public void orthogonalVectorBenchmark(ExecutionState state, Blackhole blackhole) {
		return;
	}

	@State(Scope.Thread)
	public static class ExecutionState {
		static final int SEED = 314159;
		OrthogonalVectorAlgorithm algorithm;
		int n;
		int M = OrthogonalVectorAlgorithm.M;
		int[][] input;
		Random random;

		@Setup(Level.Trial)
		public void setupTrial() {
			random = new Random(SEED);
		}

		@Setup(Level.Trial)
		public void setupIteration() {
			input = IntStream.range(0, n)
					.mapToObj(row -> IntStream.generate(() -> random.nextInt(0, 2)))
					.toArray(int[][]::new);
		}
	}
}
