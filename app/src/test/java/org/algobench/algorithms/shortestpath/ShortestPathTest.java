package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DijkstraUndirectedSP;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.assertj.core.api.Assertions;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ShortestPathTest {

	static FileInputStream file;
	static EdgeWeightedGraph denmark;
    static HashMap<Long, Integer> denmarkHashes;
	static EdgeWeightedGraph degree;
    static HashMap<Long, Integer> degreeHashes;
	static EdgeWeightedGraph dist;
    static HashMap<Long, Integer> distHashes;
	static EdgeWeightedGraph sp;
    static HashMap<Long, Integer> spHashes;
	static int n;
	static int m;


	@BeforeContainer
	public static void init() throws IOException {
        file = new FileInputStream("src/test/resources/denmark.graph");
        denmarkHashes = ParseGraph.getHashMap();
		denmark = ParseGraph.parseInput(file);

		file = new FileInputStream("src/test/resources/degree.graph");
        degreeHashes = ParseGraph.getHashMap();
		degree = ParseGraph.parseInput(file);

		file = new FileInputStream("src/test/resources/dist.graph");
        distHashes = ParseGraph.getHashMap();
		dist = ParseGraph.parseInput(file);

		file = new FileInputStream("src/test/resources/sp.graph");
        spHashes = ParseGraph.getHashMap();
		sp = ParseGraph.parseInput(file);
        file = new FileInputStream("src/test/resources/sp.graph");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(file), 1 << 16);
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		n = Integer.parseInt(tokenizer.nextToken());
		m = Integer.parseInt(tokenizer.nextToken());



	}

	@Example
	void graphHasCorrectNumberOfVertices_whenParsedFromFile() {
		Assertions.assertThat(sp.V()).isEqualTo(n);
	}

	@Example
	void graphHasCorrectNumberOfEdges_whenParsedFromFile() {
		Assertions.assertThat(sp.E()).isEqualTo(m);
	}

	@Example
	void graphHasNonNegativeWeights() {
		denmark.edges().forEach(e -> Assertions.assertThat(e.weight()).isNotNegative());
	}

	// @Example
	// void graphHasPathTo() {
	// 	BidirectionalDijkstra sp1 = new BidirectionalDijkstra(degree, 1);
	// 	BidirectionalDijkstra sp2 = new BidirectionalDijkstra(degree, 2);
	// 	BidirectionalDijkstra sp3 = new BidirectionalDijkstra(degree, 3);

	// 	Assertions.assertThat(sp1.hasPathTo(1)).isTrue();
	// }

	@Example
	void shouldFindSP() {
		// BidirectionalDijkstra sp = new BidirectionalDijkstra(test, 0);
		DijkstraEarlyStoppingBidirectional sp1 = new DijkstraEarlyStoppingBidirectional(sp, 0, 5);
		DijkstraEarlyStoppingBidirectional sp2 = new DijkstraEarlyStoppingBidirectional(sp, 3, 4);

		Assertions.assertThat(sp1.hasPathTo(5)).isTrue();
		Assertions.assertThat(sp1.distTo(5)).isEqualTo((double) 6);



	}

	@Example
	void graphEdgeWeightsSumCorrectly() {
		int sum = 0;
		for (Edge e : degree.edges())
			sum += (int) e.weight();
		Assertions.assertThat(sum).isEqualTo(30);
	}

}
