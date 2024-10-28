// package org.algobench.algorithms.shortestpath;

// import edu.princeton.cs.algs4.Edge;
// import edu.princeton.cs.algs4.EdgeWeightedGraph;
// import net.jqwik.api.*;
// import net.jqwik.api.lifecycle.BeforeContainer;
// import org.assertj.core.api.Assertions;

// import java.io.*;
// import java.util.Objects;
// import java.util.Scanner;

// public class ShortestPathTest {

// 	static File file;
// 	static EdgeWeightedGraph denmark;
// 	static EdgeWeightedGraph degree;
// 	static EdgeWeightedGraph shortestPath;
// 	static EdgeWeightedGraph test;
// 	static int n;
// 	static int m;


// 	@BeforeContainer
// 	public static void init() throws IOException {
// 		String resourceName = "denmark.graph";
// 		ClassLoader classLoader = ParseGraph.class.getClassLoader();
// 		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
// 		//denmark = ParseGraph.parseInput(new FileInputStream(file));

// 		Scanner scanner = new Scanner(file);
// 		n = scanner.nextInt();
// 		m = scanner.nextInt();

// 		resourceName = "degree.graph";
// 		classLoader = ParseGraph.class.getClassLoader();
// 		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
// 		// degree = ParseGraph.parseInput(new FileInputStream(file));

// 		resourceName = "dist.graph";
// 		classLoader = ParseGraph.class.getClassLoader();
// 		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
// 		// shortestPath = ParseGraph.parseInput(new FileInputStream(file));

// 		resourceName = "sp.graph";
// 		classLoader = ParseGraph.class.getClassLoader();
// 		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
// 		test = ParseGraph.parseInput(new FileInputStream(file));

// 	}

// 	@Example
// 	void graphHasCorrectNumberOfVertices_whenParsedFromFile() {
// 		Assertions.assertThat(denmark.V()).isEqualTo(n);
// 	}

// 	@Example
// 	void graphHasCorrectNumberOfEdges_whenParsedFromFile() {
// 		Assertions.assertThat(denmark.E()).isEqualTo(m);
// 	}

// 	@Example
// 	void graphHasNonNegativeWeights() {
// 		denmark.edges().forEach(e -> Assertions.assertThat(e.weight()).isNotNegative());
// 	}

// 	// @Example
// 	// void graphHasPathTo() {
// 	// 	BidirectionalDijkstra sp1 = new BidirectionalDijkstra(degree, 1);
// 	// 	BidirectionalDijkstra sp2 = new BidirectionalDijkstra(degree, 2);
// 	// 	BidirectionalDijkstra sp3 = new BidirectionalDijkstra(degree, 3);

// 	// 	Assertions.assertThat(sp1.hasPathTo(1)).isTrue();
// 	// }

// 	@Example
// 	void shouldFindSP() {
// 		// BidirectionalDijkstra sp = new BidirectionalDijkstra(test, 0);
// 		BidirectionalDijkstra sp1 = new BidirectionalDijkstra(test, 0, 5);
// 		BidirectionalDijkstra sp2 = new BidirectionalDijkstra(test, 3, 4);

// 		Assertions.assertThat(sp1.hasPathTo(5)).isTrue();
// 		Assertions.assertThat(sp1.distTo(5)).isEqualTo((double) 6);

// 		Assertions.assertThat(sp2.hasPathTo(4)).isTrue();
// 		Assertions.assertThat(sp2.distTo(4)).isEqualTo((double) 8);
// 	}

// 	@Example
// 	void graphEdgeWeightsSumCorrectly() {
// 		int sum = 0;
// 		for (Edge e : degree.edges())
// 			sum += (int) e.weight();
// 		Assertions.assertThat(sum).isEqualTo(30);
// 	}

// }
