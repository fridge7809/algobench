package org.algobench.benchmarking;

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import org.algobench.algorithms.shortestpath.DijkstraEarlyStopping;
import org.algobench.algorithms.shortestpath.ParseGraph;
import org.algobench.algorithms.shortestpath.DijkstraEarlyStoppingBidirectional;
import org.graalvm.collections.Pair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(value = 0)
public class PathBenchmarks {

	// report running time and average count of relaxed out
	// ./gradlew jmh
	// relax edge count in app/relax.out
	// running time in app/build/results/jmh/results.csv

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	public void benchmarkDijkstraBidirectional(Blackhole bh, ExecutionState state) {
		state.benchName = "DijkstraBi";
		for (int i = 0; i < state.pairs.length; i++) {
			int s = (int) state.pairs[i].getLeft();
			int t = (int) state.pairs[i].getRight();
			DijkstraEarlyStoppingBidirectional path = new DijkstraEarlyStoppingBidirectional(state.graph, s, t);
			bh.consume(path.distTo(t));
			state.sumRelaxed += path.getCountRelaxedEdges();
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.SECONDS)
	public void benchmarkDijkstra(Blackhole bh, ExecutionState state) {
		state.benchName = "DijkstraSingle";
		for (int i = 0; i < state.pairs.length; i++) {
			int s = (int) state.pairs[i].getLeft();
			int t = (int) state.pairs[i].getRight();
			DijkstraEarlyStopping path = new DijkstraEarlyStopping(state.graph, s, t);
			bh.consume(path.distTo(t));
			state.sumRelaxed += path.getRelaxed();
		}
	}


	@State(Scope.Benchmark)
	public static class ExecutionState {
		Random random = new Random(12345);
		EdgeWeightedGraph graph;
		Pair[] pairs;
		long sumRelaxed;
		String benchName;
		@Param({"1000"})
		private int n;

		@Setup(Level.Trial)
		public void setup() throws IOException {
			sumRelaxed = 0;
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


		@TearDown(Level.Trial)
		public void tearDown() {
			File file = new File("relax.out");
			try {
				FileWriter writer = new FileWriter(file, true);
				writer.write("average relaxed for benchmark " + benchName + ": " + sumRelaxed / n + "\n");
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
