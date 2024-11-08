package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DijkstraUndirectedSP;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeContainer;
import org.assertj.core.api.Assertions;
import org.graalvm.collections.Pair;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ShortestPathTest {

	static FileInputStream file;
	static org.algobench.algorithms.shortestpath.EdgeWeightedGraph testGraph;
	static org.algobench.algorithms.shortestpath.EdgeWeightedGraph testGraphToProcess;
	static edu.princeton.cs.algs4.EdgeWeightedGraph testGraphNormal;
	static edu.princeton.cs.algs4.EdgeWeightedGraph testGraphNormalAugmented;
	static EdgeWeightedGraph testAugmentedGraph;
	static ContractionHierarchyPreprocessor preprocessor;
	static DijkstraSP dijkstraSP;
	static int n;
	static int m;
	static int nAugmented;
	static int mAugmented;


	/**
	 * We differentiate between our own graph/edge models and the algs4 models.
	 * This is done to be able to test our own SP algorithms against the algs4 implementation.
	 * We assume algs4 has the correct implementation, which we test our modified dijkstra implementations against.
	 */
	@BeforeContainer
	static void setup() throws IOException {
        file = new FileInputStream("src/test/resources/testing.graph");
		testGraph = ParseGraph.parseGraph(file);
		file = new FileInputStream("src/test/resources/testing.graph");
		testGraphToProcess = ParseGraph.parseGraph(file);
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

		preprocessor = new ContractionHierarchyPreprocessor(testGraphToProcess);
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
	 * Edge equality is symmetric and only considered from-to node pairs.
	 * Edge(u-w) == Edge(w-u).
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
	void shouldNotFindWitnessPath() {
		LocalSearch ls = new LocalSearch(testGraph);
		int excluded = 4;
		int source = 1;
		int target = 3;
		int sum = 7 + 5;
		Assertions.assertThat(ls.hasWitnessPath(testGraph, source, target, excluded, sum)).isTrue();
	}

	@Example
	void shouldFindWitnessPath() {
		LocalSearch ls = new LocalSearch(testGraph);
		int excluded = 0;
		int source = 1;
		int target = 3;
		int sum = 1 + 12;
		Assertions.assertThat(ls.hasWitnessPath(testGraph, source, target, excluded, sum)).isTrue();
	}

	@Example
	void parsedGraphContainsDistinctRanks() {
		long distinctRanks = Arrays.stream(testAugmentedGraph.getRanks()).distinct().count();
		Assertions.assertThat(distinctRanks).isPositive();
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
		DijkstraBidirectional bidirectional = new DijkstraBidirectional(testGraph, s, t);
		DijkstraUndirectedSP dijkstra = new DijkstraUndirectedSP(testGraphNormal, s);
		Assertions.assertThat(bidirectional.distTo(t)).isEqualTo(dijkstra.distTo(t));
	}

	@Property
	void dijkstraContractionQuery_hasCorrectShortestPath(@ForAll("randomSourceTargetPairProvider") Pair<Integer, Integer> sourceTarget) {
		int s = sourceTarget.getLeft();
		int t = sourceTarget.getRight();
		DijkstraBidirectional contractionQuery = new DijkstraBidirectional(testAugmentedGraph, s, t);
		DijkstraUndirectedSP dijkstra = new DijkstraUndirectedSP(testGraphNormal, s);
		Assertions.assertThat(contractionQuery.distTo(t)).isEqualTo(dijkstra.distTo(t));
	}

	@Example
	void dijkstraContractionQuery_hasCorrectShortest() {
		int s = 0;
		int t = 4;
		DijkstraBidirectional earlyStopping = new DijkstraBidirectional(testAugmentedGraph, s, t);
		DijkstraUndirectedSP dijkstraUndirectedSP = new DijkstraUndirectedSP(testGraphNormal, s);
		Assertions.assertThat(earlyStopping.distTo(t)).isEqualTo(dijkstraUndirectedSP.distTo(t));
	}

	@Example
	void nodeContraction_returnsShortcutsAdded() {
		Assertions.assertThat(preprocessor.simulateContraction(0)).isEqualTo(1);
	}

	@Example
	void nodeRank_isCalculatedCorrectly() throws Exception {
		// Test execution order may effect when a preprocessor mutates the internal graph of the preprocessor
		// Init a new one for each test execution. Messy, but it works :)
		file = new FileInputStream("src/test/resources/testing.graph");
		testGraphToProcess = ParseGraph.parseGraph(file);
		preprocessor = new ContractionHierarchyPreprocessor(testGraphToProcess);
		Assertions.assertThat(preprocessor.calculateRank(0)).isEqualTo(-1);
		Assertions.assertThat(preprocessor.calculateRank(1)).isEqualTo(5);
		Assertions.assertThat(preprocessor.calculateRank(2)).isEqualTo(-2);
		Assertions.assertThat(preprocessor.calculateRank(3)).isEqualTo(-0);
		Assertions.assertThat(preprocessor.calculateRank(4)).isEqualTo(-2);
		Assertions.assertThat(preprocessor.calculateRank(5)).isEqualTo(-1);
		Assertions.assertThat(preprocessor.calculateRank(6)).isEqualTo(-1);
	}

	@Example
	void nodesContractionOrderIsSortedAccordingToRank() {
		preprocessor = new ContractionHierarchyPreprocessor(testGraphToProcess);
		preprocessor.preprocess();
		Assertions.assertThat(preprocessor.getContractionOrder()).isSortedAccordingTo(preprocessor.getNodeComparator());
	}

	// If some edge equals another edge, it has the same weight
	@Property
	void graphsHaveIdenticalEdgeWeights_whenParsedFromFile(@ForAll("weightPairProvider") Tuple.Tuple2 weights) {
		Assertions.assertThat(weights.get1().equals(weights.get2())).isTrue();
	}

	@Provide
	Arbitrary<Tuple.Tuple2> weightPairProvider() {
		return Arbitraries.of(testGraph.allEdges())
				.tuple2()
				.filter(tuple -> tuple.get1().equals(tuple.get2()))
				.map(tuple -> Tuple.of(tuple.get1().weight(), tuple.get2().weight()));
	}


	@Provide
	Arbitrary<Pair<Integer, Integer>> randomSourceTargetPairProvider() {
		return Arbitraries.integers()
				.between(0, n - 1)
				.flatMap(s -> Arbitraries.integers().between(0, n - 1).map(t -> Pair.create(s, t)));
	}
}