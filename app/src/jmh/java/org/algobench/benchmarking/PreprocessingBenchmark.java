package org.algobench.benchmarking;

import org.algobench.algorithms.shortestpath.*;
import org.graalvm.collections.Pair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(value = 0)
public class PreprocessingBenchmark {

	// report running time and average count of relaxed out
	// ./gradlew jmh
	// relax edge count in app/relax.out
	// running time in app/build/results/jmh/results.csv

	@Benchmark
	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	public void benchmarkDijkstraBidirectional(Blackhole bh, ExecutionState state) {
		bh.consume(state.contractionHierachiesPreprocessor);
	}


	@State(Scope.Benchmark)
	public static class ExecutionState {
		EdgeWeightedGraph graph;
		ContractionHierachiesPreprocessor contractionHierachiesPreprocessor;

		@Setup(Level.Trial)
		public void setup() throws IOException {
			String resourceName = "denmark.graph";
			ClassLoader classLoader = ParseGraph.class.getClassLoader();
			try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {
				if (inputStream == null) {
					throw new FileNotFoundException("Resource " + resourceName + " not found");
				}
				graph = ParseGraph.parseInput(inputStream);
			}
			contractionHierachiesPreprocessor = new ContractionHierachiesPreprocessor(graph);
		}
	}
}
