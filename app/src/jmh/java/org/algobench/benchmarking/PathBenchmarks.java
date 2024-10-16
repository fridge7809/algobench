package org.algobench.benchmarking;

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import org.algobench.algorithms.shortestpath.ParseGraph;
import org.algobench.algorithms.shortestpath.BidirectionalDijkstra;
import org.graalvm.collections.Pair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PathBenchmarks {

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(value = 1, warmups = 1)
	public void benchmarkDijkstra(Blackhole bh, ExecutionState state) {
		for (int i = 0; i < state.pairs.length; i++) {
			int s = (int) state.pairs[i].getLeft();
			int t = (int) state.pairs[i].getLeft();
			bh.consume(new BidirectionalDijkstra(state.graph, s, t).distTo(t));
		}
	}


	@State(Scope.Benchmark)
	public static class ExecutionState {
		Random random = new Random(12345);
		EdgeWeightedGraph graph;
		Pair[] pairs;
		@Param({"1000"})
		private int n;

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
			pairs = new Pair[n];
			for (int i = 0; i < n; i++) {
				pairs[i] = Pair.create(random.nextInt(0, graph.V()), random.nextInt(0, graph.V()));
			}
		}

	}
}
