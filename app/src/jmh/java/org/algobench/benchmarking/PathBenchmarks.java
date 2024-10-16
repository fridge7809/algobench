package org.algobench.benchmarking;

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import org.algobench.algorithms.hashing.MatrixVectorHash;
import org.algobench.algorithms.shortestpath.BidirectionalDijkstra;
import org.algobench.algorithms.shortestpath.DijkstraShortestPath;
import org.graalvm.collections.Pair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
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
			bh.consume(new DijkstraShortestPath(state.graph, s).distTo(t));
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
			ClassLoader classLoader = BidirectionalDijkstra.class.getClassLoader();
			File file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
			graph = BidirectionalDijkstra.parseInput(new FileInputStream(file));
			pairs = new Pair[n];
			for (int i = 0; i < n; i++) {
				pairs[i] = Pair.create(random.nextInt(0, graph.V()), random.nextInt(0, graph.V()));
			}
		}
	}
}
