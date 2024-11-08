package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.Bag;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class ContractionHierarchyPreprocessor {

	private final Set<Edge> visitedEdges;
	private final EdgeWeightedGraph graph;
	private final LocalSearch localSearch;
	private final int[] rank;
	private final Set<Edge> shortcuts;
	private final int[] deletedNeighbors;
	private PriorityQueue<Integer> contractionQueue;

	public ContractionHierarchyPreprocessor(EdgeWeightedGraph graph) {
		this.graph = graph;
		this.rank = new int[graph.V()];
		this.shortcuts = HashSet.newHashSet(570193);
		this.deletedNeighbors = new int[graph.V()];
		this.visitedEdges = HashSet.newHashSet(graph.E());
		this.localSearch = new LocalSearch(graph);
		this.contractionQueue = new PriorityQueue<>(graph.V(), new NodeComparator());
		preprocess();
	}

	public static void main(String[] args) {
		try (FileInputStream fis = new FileInputStream("app/src/test/resources/denmark.graph"); FileWriter fw = new FileWriter("denmark_processed.graph")) {
			EdgeWeightedGraph graph = ParseGraph.parseInput(fis);
			ContractionHierarchyPreprocessor ch = new ContractionHierarchyPreprocessor(graph);
			System.out.println(ch.shortcuts.size());

			// Format augmented graph
			StringBuilder sb = new StringBuilder();
			sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");

			for (int i = 0; i < ch.graph.V(); i++) {
				sb.append(i).append(" ").append(ch.rank[i]).append("\n");
			}
			for (Edge e : ch.graph.edges()) {
				sb.append(e).append(e.isShortcut() ? " 1" : " -1").append("\n");
			}

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int calculateRank(int node) {
		int amountOfShortcuts = simulateContraction(node);
		int edgeDifference = (amountOfShortcuts - graph.degree(node));
		return edgeDifference + deletedNeighbors[node];
	}

	/**
	 * Creates an initial node contraction ordering, then begins contraction process.
	 * Lazy updates rank as it is contracting.
	 */
	private int preprocess() {
		long startTime = System.currentTimeMillis();
		System.out.println("Begin contraction");
		initNodeOrder();
		int lazyUpdates = 0;
		int lazyUpdatesLimit = 50;

		while (!contractionQueue.isEmpty()) {
			int candidate = contractionQueue.poll();
			int candidateRank = rank[candidate];
			if (contractionQueue.peek() != null && calculateRank(contractionQueue.peek()) < candidateRank) {
				rank[candidate] = calculateRank(candidate);
				contractionQueue.add(candidate);
				lazyUpdates++;
				if (lazyUpdates > lazyUpdatesLimit) {
					contractionQueue = updateAllPriorities();
					lazyUpdates = 0;
				}
			} else {
				rank[candidate] = candidateRank;
				contractNode(candidate);
				signalDeletionToNeighbours(candidate);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Contraction time total: " + (endTime - startTime) / 1000.0D + " seconds.");
		System.out.println("Shortcuts added: " + shortcuts.size());
		return shortcuts.size();
	}

	private void signalDeletionToNeighbours(int node) {
		for (Edge edge : graph.getAdjacentEdges(node)) {
			deletedNeighbors[edge.other(node)]++;
		}
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

					Edge shortcut = new Edge(u, w, sumWeight, true);
					if (visitedEdges.contains(shortcut) || shortcutsCreated.contains(shortcut)) {
						continue;
					}

					if (localSearch.hasWitnessPath(rank, u, w, node, sumWeight)) {
						shortcutsCreated.add(shortcut);
						shortcuts.add(shortcut);
						graph.addEdge(shortcut);
					}
				}
			}
		}
		visitedEdges.addAll(shortcutsCreated);
		for (Edge e : adjacentEdges) {
			visitedEdges.add(e);
		}
		return shortcutsCreated.size();
	}

	/**
	 * An approximation for the amount of shortcuts to add when contracting a node.
	 * Uses Fast 1-hop local search to find witness paths.
	 */
	public int simulateContraction(int node) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<Edge> simulatedShortcuts = new HashSet<>();
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (!k.equals(j)) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					if (!hasWitnessPath(u, w, sumWeight)) {
						Edge shortcut = new Edge(u, w, sumWeight, true);
						simulatedShortcuts.add(shortcut);
					}
				}
			}
		}
		return simulatedShortcuts.size();
	}

	/**
	 * Fast local 1-hop search for witness paths.
	 */
	private boolean hasWitnessPath(int u, int w, double sumWeight) {
		for (Edge edge : graph.getAdjacentEdges(u)) {
			if (edge.other(u) == w && edge.weight() < sumWeight) {
				return true;
			}
		}
		return false;
	}

	private class NodeComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return Integer.compare(rank[o1], rank[o2]);
		}
	}
}