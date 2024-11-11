package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.Bag;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class ContractionHierarchyPreprocessor {

	private final EdgeContainer visitedEdges;
	private final EdgeWeightedGraph graph;
	private final LocalSearch localSearch;
	private int[] rank = new int[0];
	private final List<Integer> contractionOrder;
	private final EdgeContainer shortcuts;
	private final int[] deletedNeighbors;
	private PriorityQueue<Integer> contractionQueue;
	private final NodeComparator nodeComparator;

	public ContractionHierarchyPreprocessor(EdgeWeightedGraph graph) {
		this.graph = graph;
		this.rank = new int[graph.V()];
		this.shortcuts = new EdgeContainer();
		this.deletedNeighbors = new int[graph.V()];
		this.visitedEdges = new EdgeContainer();
		this.localSearch = new LocalSearch(graph);
		this.contractionOrder = new ArrayList<>(graph.V());
		this.contractionQueue = new PriorityQueue<>(graph.V(), new NodeComparator());
		this.nodeComparator = new NodeComparator();
	}

	public static void main(String[] args) {
		writeAugmentedGraphToFile("app/src/test/resources/denmark.graph", "denmark_processed.graph");
	}

	public static void writeAugmentedGraphToFile(String inputGraphPath, String outputGraphPath) {
		try (FileInputStream fis = new FileInputStream(inputGraphPath);
				FileWriter fw = new FileWriter(outputGraphPath)) {
			EdgeWeightedGraph graph = ParseGraph.parseGraph(fis);
			ContractionHierarchyPreprocessor ch = new ContractionHierarchyPreprocessor(graph);
			ch.preprocess();
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

	public int calculateRank(int node) {
		int amountOfShortcuts = simulateContraction(node);
		int edgeDifference = (amountOfShortcuts - graph.degree(node));
		return edgeDifference + deletedNeighbors[node];
	}

	/**
	 * Creates an initial node contraction ordering, then begins contraction
	 * process.
	 * Lazy updates rank as it is contracting.
	 */
	public int preprocess() {
		long startTime = System.currentTimeMillis();
		System.out.println("Begin contraction");
		initNodeOrder();
		int lazyUpdates = 0;
		int lazyUpdatesLimit = 50;
		int contractedNodes = 0;

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
				contractionOrder.add(candidate);
				contractedNodes++;
				if(contractedNodes % 2000 == 0) System.out.println(contractedNodes);
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
			if (rank[j.other(node)] > rank[node]) {
				for (Edge k : adjacentEdges) {
					if (!k.equals(j) && rank[k.other(node)] > rank[node]) {
						int u = j.other(node);
						int w = k.other(node);
						double sumWeight = j.weight() + k.weight();

						if (visitedEdges.containsEdge(node, u) || visitedEdges.containsEdge(node, w) || shortcuts.containsEdge(u, w)) {
							continue;
						}

						Edge shortcut = new Edge(u, w, sumWeight, true);

						if (!localSearch.hasWitnessPath(graph, u, w, node, sumWeight)) {
							shortcutsCreated.add(shortcut);
							shortcuts.addEdge(u, w);
							graph.addEdge(shortcut);
						}
					}
				}
			}
		}
		for (Edge e : adjacentEdges) {
			visitedEdges.addEdge(node, e.other(node));;
		}
		//System.out.println(shortcutsCreated + " shortcuts for node: " + node);
		return shortcutsCreated.size();
	}

	/**
	 * An approximation for the number of shortcuts to add when contracting a node.
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