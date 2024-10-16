package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.assertj.core.api.Assertions;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class ShortestPathTest {

	static File file;
	static EdgeWeightedGraph denmark;
	static EdgeWeightedGraph degree;
	static EdgeWeightedGraph shortestPath;
	static int n;
	static int m;


	@BeforeContainer
	public static void init() throws IOException {
		String resourceName = "denmark.graph";
		ClassLoader classLoader = BidirectionalDijkstra.class.getClassLoader();
		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
		denmark = BidirectionalDijkstra.parseInput(new FileInputStream(file));

		Scanner scanner = new Scanner(file);
		n = scanner.nextInt();
		m = scanner.nextInt();

		resourceName = "degree.graph";
		classLoader = BidirectionalDijkstra.class.getClassLoader();
		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
		degree = BidirectionalDijkstra.parseInput(new FileInputStream(file));

		resourceName = "dist.graph";
		classLoader = BidirectionalDijkstra.class.getClassLoader();
		file = new File(Objects.requireNonNull(classLoader.getResource(resourceName)).getFile());
		shortestPath = BidirectionalDijkstra.parseInput(new FileInputStream(file));

	}

	@Example
	void graphHasCorrectNumberOfVertices_whenParsedFromFile() {
		Assertions.assertThat(denmark.V()).isEqualTo(n);
	}

	@Example
	void graphHasCorrectNumberOfEdges_whenParsedFromFile() {
		Assertions.assertThat(denmark.E()).isEqualTo(m);
	}

	@Example
	void graphHasNonNegativeWeights() {
		denmark.edges().forEach(e -> Assertions.assertThat(e.weight()).isNotNegative());
	}

	@Example
	void graphHasPathTo() {
		DijkstraShortestPath sp1 = new DijkstraShortestPath(degree, 1);
		DijkstraShortestPath sp2 = new DijkstraShortestPath(degree, 2);
		DijkstraShortestPath sp3 = new DijkstraShortestPath(degree, 3);

		Assertions.assertThat(sp1.hasPathTo(1)).isTrue();
	}

	@Example
	void graphEdgeWeightsSumCorrectly() {
		int sum = 0;
		for (Edge e : degree.edges())
			sum += (int) e.weight();
		Assertions.assertThat(sum).isEqualTo(30);
	}

}
