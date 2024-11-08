package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DijkstraUndirectedSP;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.assertj.core.api.Assertions;
import org.graalvm.collections.Pair;

import java.io.*;
import java.util.StringTokenizer;

public class ShortestPathTest {

	static FileInputStream file;
	static org.algobench.algorithms.shortestpath.EdgeWeightedGraph testGraph;
	static edu.princeton.cs.algs4.EdgeWeightedGraph testGraphNormal;
	static edu.princeton.cs.algs4.EdgeWeightedGraph testGraphNormalAugmented;
	static EdgeWeightedGraph testAugmentedGraph;
	static DijkstraSP dijkstraSP;
	static int n;
	static int m;
	static int nAugmented;
	static int mAugmented;


	/**
	 * We differentiate between our own graph/edge models and the algs4 models.
	 * This is done to be able to test our own SP algorithms against the algs4 implementation.
	 * We assume algs4 has the correct implementation.
	 */
	@BeforeContainer
	static void setup() throws IOException {
        file = new FileInputStream("src/test/resources/testing.graph");
		testGraph = ParseGraph.parseGraph(file);
		file = new FileInputStream("src/test/resources/testing.graph");
		testGraphNormal = ParseGraph.parseAlgsGraph(file);
		file = new FileInputStream("src/test/resources/testing.graph");
		ContractionHierarchyPreprocessor.writeAugmentedGraphToFile("src/test/resources/testing.graph", "src/test/resources/testing_augmented.graph");
		file = new FileInputStream("src/test/resources/testing_augmented.graph");
		testAugmentedGraph = ParseGraphAugmented.parseAugmentedGraph(file);
		file = new FileInputStream("src/test/resources/testing_augmented.graph");
		testGraphNormalAugmented = ParseGraphAugmented.parseAugmentedAlgsGraph(file);

		file = new FileInputStream("src/test/resources/testing.graph");
		BufferedReader reader = new BufferedReader(new InputStreamReader(file), 1 << 16);
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		n = Integer.parseInt(tokenizer.nextToken());
		m = Integer.parseInt(tokenizer.nextToken());

		file = new FileInputStream("src/test/resources/testing_augmented.graph");
		reader = new BufferedReader(new InputStreamReader(file), 1 << 16);
		tokenizer = new StringTokenizer(reader.readLine());
		nAugmented = Integer.parseInt(tokenizer.nextToken());
		mAugmented = Integer.parseInt(tokenizer.nextToken());
	}

	@Example
	void graphHasCorrectNumberOfVerticies_whenParsedFromFile() {
		Assertions.assertThat(testGraph.V()).isEqualTo(n);
	}

	@Example
	void graphHasCorrectNumberOfEdges_whenParsedFromFile() {
		Assertions.assertThat(testGraph.E()).isEqualTo(m);
	}


	/**
	 * Contraction hierarchy solution relies on correct equality operation.
	 * Edge equality is symmetric and only considers from-to node pairs
	 * Edge(u-w) == Edge(w-u)
	 */
	@Example
	void graphContainsEdges_whenParsedFromFile() {
		Assertions.assertThat(testGraph.containsEdge(new Edge(0, 1, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(0, 3, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(1, 2, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(1, 4, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(1, 5, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(1, 6, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(2, 3, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(2, 4, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(3, 4, 0.0, false))).isTrue();
	}

	@Example
	void graphContainsSymmetricEdges_whenParsedFromFile() {
		// These edges are not in the file, but they should be true because of symmetric .equals()
		Assertions.assertThat(testGraph.containsEdge(new Edge(1, 0, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(3, 0, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(2, 1, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(4, 1, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(5, 1, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(6, 1, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(3, 2, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(4, 2, 0.0, false))).isTrue();
		Assertions.assertThat(testGraph.containsEdge(new Edge(4, 3, 0.0, false))).isTrue();
	}

	@Example
	void graphDoesNotContainEdges_notPresentInFile() {
		Assertions.assertThat(testGraph.containsEdge(new Edge(0, 0, 0.0, false))).isFalse();
	}

	@Example
	void graphNormalHasCorrectNumberOfEdges_whenParsedFromFile() {
		Assertions.assertThat(testGraphNormal.E()).isEqualTo(m);
	}

	@Example
	void graphNormalHasCorrectNumberOVerticies_whenParsedFromFile() {
		Assertions.assertThat(testGraphNormal.V()).isEqualTo(n);
	}

	@Example
	void graphNormalAugmentedHasCorrectNumberOfEdges_whenParsedFromFile() {
		Assertions.assertThat(testGraphNormalAugmented.E()).isEqualTo(mAugmented);
	}

	@Example
	void graphNormalAugmentedHasCorrectNumberOVerticies_whenParsedFromFile() {
		Assertions.assertThat(testGraphNormalAugmented.V()).isEqualTo(nAugmented);
	}

	@Example
	void graphAugmentedHasCorrectNumberOfVerticies_whenParsedFromFile() {
		Assertions.assertThat(testAugmentedGraph.V()).isEqualTo(nAugmented);
	}

	@Example
	void graphAugmentedHasCorrectNumberOfEdges_whenParsedFromFile() {
		Assertions.assertThat(testAugmentedGraph.E()).isEqualTo(mAugmented);
	}

	@Example
	void shortcutsShouldAscendInRank(@ForAll("shortcutProvider") Edge shortcut) {
		int[] ranks = testAugmentedGraph.getRanks();
		int u = shortcut.either();
		int w = shortcut.other(u);
		Assertions.assertThat(ranks[u]).isGreaterThanOrEqualTo(ranks[w]);
	}

	@Provide
	Arbitrary<Edge> shortcutProvider() {
		return Arbitraries.of(testAugmentedGraph.allEdges()).filter(Edge::isShortcut);
	}

	@Property
	void dijkstraWithEarlyStopping_hasCorrectShortestPath(@ForAll("randomSourceTargetPairProvider") Pair<Integer, Integer> sourceTarget) {
		int s = sourceTarget.getLeft();
		int t = sourceTarget.getRight();
		DijkstraEarlyStopping earlyStopping = new DijkstraEarlyStopping(testGraph, s, t);
		DijkstraUndirectedSP dijkstraUndirectedSP = new DijkstraUndirectedSP(testGraphNormal, s);
		Assertions.assertThat(earlyStopping.distTo(t)).isEqualTo(dijkstraUndirectedSP.distTo(t));
	}

	@Property
	void dijkstraBidirectional_hasCorrectShortestPath(@ForAll("randomSourceTargetPairProvider") Pair<Integer, Integer> sourceTarget) {
		int s = sourceTarget.getLeft();
		int t = sourceTarget.getRight();
		DijkstraEarlyStoppingBidirectional earlyStopping = new DijkstraEarlyStoppingBidirectional(testGraph, s, t);
		DijkstraUndirectedSP dijkstraUndirectedSP = new DijkstraUndirectedSP(testGraphNormal, s);
		Assertions.assertThat(earlyStopping.distTo(t)).isEqualTo(dijkstraUndirectedSP.distTo(t));
	}

	@Property
	void dijkstraContractionQuery_hasCorrectShortestPath(@ForAll("randomSourceTargetPairProvider") Pair<Integer, Integer> sourceTarget) {
		int s = sourceTarget.getLeft();
		int t = sourceTarget.getRight();
		DijkstraContractionQuery earlyStopping = new DijkstraContractionQuery(testAugmentedGraph, s, t);
		DijkstraUndirectedSP dijkstraUndirectedSP = new DijkstraUndirectedSP(testGraphNormal, s);
		Assertions.assertThat(earlyStopping.distTo(t)).isEqualTo(dijkstraUndirectedSP.distTo(t));
	}

	@Provide
	Arbitrary<Pair<Integer, Integer>> randomSourceTargetPairProvider() {
		return Arbitraries.integers()
				.between(0, n - 1)
				.flatMap(s -> Arbitraries.integers().between(0, n - 1).map(t -> Pair.create(s, t)));
	}
}