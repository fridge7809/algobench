package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.Bag;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class ContractionHierarchyPreprocessor {

	private final Set<Edge> visitedEdges;
	private final EdgeWeightedGraph graph;
	private final LocalSearch localSearch;
	private int[] rank = new int[0];
	private final List<Integer> contractionOrder;
	private final Set<Edge> shortcuts;
	private final int[] deletedNeighbors;
	private PriorityQueue<Integer> contractionQueue;
	private final NodeComparator nodeComparator;

	public ContractionHierarchyPreprocessor(EdgeWeightedGraph graph) {
		this.graph = graph;
		this.rank = new int[graph.V()];
		this.shortcuts = HashSet.newHashSet(570193);
		this.deletedNeighbors = new int[graph.V()];
		this.visitedEdges = HashSet.newHashSet(graph.E());
		this.localSearch = new LocalSearch(graph);
		this.contractionOrder = new ArrayList<>(graph.V());
		this.contractionQueue = new PriorityQueue<>(graph.V(), new NodeComparator());
		this.nodeComparator = new NodeComparator();
	}

	public static void main(String[] args) {
		writeAugmentedGraphToFile("app/src/test/resources/denmark.graph", "denmark_processed.graph");
	}

	public static void writeAugmentedGraphToFile(String inputGraphPath, String outputGraphPath) {
		try (FileInputStream fis = new FileInputStream(inputGraphPath); FileWriter fw = new FileWriter(outputGraphPath)) {
			EdgeWeightedGraph graph = ParseGraph.parseGraph(fis);
			ContractionHierarchyPreprocessor ch = new ContractionHierarchyPreprocessor(graph);
			ch.preprocess();
			System.out.println(ch.shortcuts.size());

			// Format augmented graph
			StringBuilder sb = new StringBuilder();
			sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");

			for (int i = 0; i < ch.graph.V(); i++) {
				sb.append(i).append(" ").append(graph.getCoords().get(i).getLeft()).append(" ").append(graph.getCoords().get(i).getRight()).append(" ").append(ch.rank[i]).append("\n");
			}
			for (Edge e : ch.graph.edges()) {
				sb.append(e).append(e.isShortcut() ? " 1" : " -1").append("\n");
			}

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int calculateRank(int node) {
		int amountOfShortcuts = simulateContraction(node);
		int edgeDifference = (amountOfShortcuts - graph.degree(node));
		return edgeDifference + deletedNeighbors[node];
	}

	/**
	 * Creates an initial node contraction ordering, then begins contraction process.
	 * Lazy updates rank as it is contracting.
	 */
	public int preprocess() {
		long startTime = System.currentTimeMillis();
		initNodeOrder();
		System.out.println("Begin contraction");
		int lazyUpdates = 0;
		int lazyUpdatesLimit = 5000;

		while (!contractionQueue.isEmpty()) {
			int candidate = contractionQueue.poll();
			int candidateRank = rank[candidate];
			if (contractionQueue.peek() != null && calculateRank(contractionQueue.peek()) < candidateRank) {
				rank[candidate] = calculateRank(candidate);
				contractionQueue.add(candidate);
				lazyUpdates++;
				if (lazyUpdates > lazyUpdatesLimit) {
					contractionQueue = updateAllPriorities();
					System.out.println("reorder pq");
					lazyUpdates = 0;
				}
			} else {
				rank[candidate] = candidateRank;
				contractionOrder.add(candidate);
				contractNode(candidate);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Contraction time total: " + (endTime - startTime) / 1000.0D + " seconds.");
		System.out.println("Shortcuts added: " + shortcuts.size());
		return shortcuts.size();
	}

	private PriorityQueue<Integer> updateAllPriorities() {
		PriorityQueue<Integer> newQueue = new PriorityQueue<>(contractionQueue.size(), new NodeComparator());
		for (int node : contractionQueue) {
			rank[node] = calculateRank(node);
			newQueue.add(node);
		}
		return newQueue;
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

	public int contractNode(int node) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<Edge> shortcutsCreated = new HashSet<>();

		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (!k.equals(j)) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					if ((rank[u] > rank[w])) {
						continue;
					}

					Edge shortcut = new Edge(u, w, sumWeight, true);
					if (visitedEdges.contains(shortcut) || shortcutsCreated.contains(shortcut)) {
						continue;
					}

					if (!localSearch.hasWitnessPath(graph, u, w, node, sumWeight)) {
						shortcutsCreated.add(shortcut);
						shortcuts.add(shortcut);
						graph.addEdge(shortcut);
					}
				}
			}
		}
		for (Edge e : adjacentEdges) {
			visitedEdges.add(e);
			deletedNeighbors[e.other(node)]++;
		}
		return shortcutsCreated.size();
	}

	/**
	 * An approximation for the number of shortcuts to add when contracting a node.
	 * Uses Fast 1-hop local search to find witness paths.
	 */
	public int simulateContraction(int node) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		int shortcuts = 0;
		int cost = 0;
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (!k.equals(j)) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					if (hasWitnessPath(u, w, sumWeight) == -1) {
						shortcuts++;
					}
				}
			}
		}
		return shortcuts;
	}

	/**
	 * Fast local 1-hop search for witness paths.
	 */
	private int hasWitnessPath(int u, int w, double sumWeight) {
		int counter = 0;
		for (Edge edge : graph.getAdjacentEdges(u)) {
			counter++;
			if (edge.other(u) == w && edge.weight() < sumWeight) {
				return counter;
			}
		}
		return -1;
	}

	public NodeComparator getNodeComparator() {
		return nodeComparator;
	}

	private class NodeComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return Integer.compare(rank[o1], rank[o2]);
		}
	}

	public List<Integer> getContractionOrder() {
		return contractionOrder;
	}

	public int[] getRank() {
		return rank;
	}
}