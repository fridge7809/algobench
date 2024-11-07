package org.algobench.algorithms.shortestpath;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class ContractionHierachiesPreprocessor {

	private final HashMap<Integer, Set<Integer>> visitedEdges;
	private final EdgeWeightedGraph graph;
	private final LocalSearch localSearch;
	private final int[] rank;
	private final Set<Edge> shortcuts;
	private final PriorityQueue<Integer> contractionQueue;
	private final int[] deletedNeighbors;

	public ContractionHierachiesPreprocessor(EdgeWeightedGraph graph) {
		this.graph = graph;
		this.rank = new int[graph.V()];
		this.shortcuts = new HashSet<>();
		this.deletedNeighbors = new int[graph.V()];
		this.visitedEdges = new HashMap<>();
		this.localSearch = new LocalSearch(graph);
		this.contractionQueue = new PriorityQueue<>((a, b) -> Integer.compare(rank[a], rank[b]));
		preprocess();
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraph.parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
			ContractionHierachiesPreprocessor ch = new ContractionHierachiesPreprocessor(graph);
			System.out.println(ch.shortcuts.size());

			// Format augmented graph
			StringBuilder sb = new StringBuilder();
			sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");
			for (int i = 0; i < ch.graph.V(); i++) {
				sb.append(i).append(" ").append(ch.rank[i]).append("\n");
			}
			for (Edge e : ch.graph.edges()) {
				if (e.isShortcut()) {
					sb.append(e).append(" 1").append("\n");
				} else {
					sb.append(e).append(" -1").append("\n");
				}
			}
			File output = new File("denmark_processed.graph");
			FileWriter fw = new FileWriter(output);
			fw.write(sb.toString());
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<Edge> getShortcuts() {
		return shortcuts;
	}

	private int calculateRank(int node) {
		int amountOfShortcuts = simulateContraction(node);
		int edgeDifference = (amountOfShortcuts - graph.degree(node));
		return edgeDifference + deletedNeighbors[node];
	}

	/**
	 * Creates an initial node contraction ordering, then begins contraction process.
	 * Lazy updates rank
	 */
	private void preprocess() {
		initNodeOrder();
		while (!contractionQueue.isEmpty()) {
			int candidate = contractionQueue.poll();
			int candidateRank = rank[candidate];
			if (contractionQueue.peek() != null && calculateRank(contractionQueue.peek()) < candidateRank) {
				contractionQueue.add(candidate);
			} else {
				rank[candidate] = candidateRank;
				contractNode(candidate);
				signalDeletionToNeighbours(candidate);
			}
		}
	}

	private void signalDeletionToNeighbours(int node) {
		graph.getAdjacentEdges(node).stream()
				.map(edge -> edge.other(node))
				.forEach(other -> deletedNeighbors[other]++);
	}

	/**
	 * Initialize contractionQueue with an approximate rank.
	 */
	private void initNodeOrder() {
		IntStream.range(0, graph.V()).forEach(i -> {
			rank[i] = calculateRank(i);
			contractionQueue.add(i);
		});
	}

	public void contractNode(int node) {
		List<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		HashMap<Integer, Set<Integer>> shortcutTracker = new HashMap<>();
		Set<Integer> ignoringEdgesBetweenTheseAndNode = new HashSet<>();

		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (!k.equals(j)) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					if (!edgeExistsIn(u, w, visitedEdges)) {
						if (localSearch.hasWitnessPath(rank, u, w, node, sumWeight)) {
							if (!edgeExistsIn(u, w, shortcutTracker)) {
								Set<Integer> setOfAssociatedShortcuts = shortcutTracker.get(u);
								if (setOfAssociatedShortcuts == null) {
									setOfAssociatedShortcuts = new HashSet<>();
								}
								setOfAssociatedShortcuts.add(w);
								shortcutTracker.put(u, setOfAssociatedShortcuts);
								shortcuts.add(new Edge(u, w, sumWeight, true));
								graph.addEdge(new Edge(u, w, sumWeight, true));
							}
						}
						localSearch.emptyQueue();
					}
					ignoringEdgesBetweenTheseAndNode.add(u);
					ignoringEdgesBetweenTheseAndNode.add(w);
				}
			}
		}
		visitedEdges.put(node, ignoringEdgesBetweenTheseAndNode);
	}

	/**
	 * An approximation for the amount of shortcuts to add when contracting a node.
	 * Uses Fast 1-hop local search to find witness paths.
	 */
	public int simulateContraction(int node) {
		List<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<String> simulatedShortcuts = new HashSet<>();
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (!k.equals(j)) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					boolean witnessPath = hasWitnessPath(u, w, sumWeight);

					if (witnessPath) {
						continue;
					}

					String edgeKey = createEdgeKey(u, w);
					simulatedShortcuts.add(edgeKey);
				}
			}
		}
		return simulatedShortcuts.size();
	}

	/**
	 * Fast local 1-hop search for witness paths.
	 */
	private boolean hasWitnessPath(int u, int w, double sumWeight) {
		return graph.getAdjacentEdges(u).stream()
				.anyMatch(e -> e.other(u) == w && e.weight() < sumWeight);
	}

	/**
	 * Key to use for edges in data structures that are backed by a hash function.
	 */
	private String createEdgeKey(int u, int w) {
		return u < w ? u + "-" + w : w + "-" + u;
	}

	private boolean edgeExistsIn(int u, int w, HashMap<Integer, Set<Integer>> mapOfEdges) {
		if (mapOfEdges.containsKey(u) && mapOfEdges.get(u).contains(w)) {
			return true;
		}
		return mapOfEdges.containsKey(w) && mapOfEdges.get(w).contains(u);
	}
}