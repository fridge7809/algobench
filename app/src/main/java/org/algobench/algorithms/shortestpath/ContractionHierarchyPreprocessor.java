package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.Bag;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class ContractionHierarchyPreprocessor {

	private final EdgeWeightedGraph graph;
	private final LocalSearch localSearch;
	private int[] rank;
	private final int[] contractionOrder;
	private final EdgeContainer shortcuts;
	private final int[] deletedNeighbors;
	private PriorityQueue<Integer> contractionQueue;
	private final NodeComparator nodeComparator;
	private int nodeDegrees;
	private int averageNodeDegree;

	public ContractionHierarchyPreprocessor(EdgeWeightedGraph graph) {
		this.graph = graph;
		this.rank = new int[graph.V()];
		this.shortcuts = new EdgeContainer();
		this.deletedNeighbors = new int[graph.V()];
		this.localSearch = new LocalSearch(graph);
		this.contractionOrder = new int[graph.V()];
		this.contractionQueue = new PriorityQueue<>(graph.V(), new NodeComparator());
		this.nodeComparator = new NodeComparator();
	}

	public static void main(String[] args) {
		writeAugmentedGraphToFile("app/src/test/resources/denmark.graph", "denmark_test_processed.graph");
	}

	public static void writeAugmentedGraphToFile(String inputGraphPath, String outputGraphPath) {
		try (FileInputStream fis = new FileInputStream(inputGraphPath);
				FileWriter fw = new FileWriter(outputGraphPath)) {
			EdgeWeightedGraph graph = ParseGraph.parseGraph(fis);
			ContractionHierarchyPreprocessor ch = new ContractionHierarchyPreprocessor(graph);
			ch.preprocess();

			// Format augmented graph
			StringBuilder sb = new StringBuilder();
			sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");

			for (int i = 0; i < ch.graph.V(); i++) {
				sb.append(i).append(" ").append(graph.getCoords().get(i).getLeft()).append(" ")
						.append(graph.getCoords().get(i).getRight()).append(" ").append(ch.contractionOrder[i])
						.append("\n");
			}
			for (Edge e : ch.graph.edges()) {
				String[] fromto = e.toString().split("-");
				Long from = Long.parseLong(fromto[0]);
				Long to = Long.parseLong(fromto[1].split(" ")[0]);
				Long isShortcut = Long.parseLong(e.isShortcut() ? "1" : "-1"); // "contracted"
				int weight = (int) e.weight();

				sb.append(from).append(" ").append(to).append(" ").append(weight).append(" ").append(isShortcut)
						.append("\n");
			}

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int calculateRank(int node, boolean recalculate) {
		int amountOfShortcuts;
		if (recalculate)
			amountOfShortcuts = totalReorderContractSimulation(node);
		else
			amountOfShortcuts = simulateContraction(node);
		int edgeDifference = (amountOfShortcuts - graph.degree(node));
		return edgeDifference + deletedNeighbors[node];
	}

	/**
	 * Creates an initial node contraction ordering, then begins actual contraction
	 * process.
	 * Lazy updates rank as it is contracting. When lazy updates has exceeded
	 * lazyUpdatesLimit, entire pq is reranked.
	 */
	public int preprocess() {
		long startTime = System.currentTimeMillis();
		System.out.println("Begin contraction");
		initNodeOrder();
		int lazyUpdates = 0;
		int lazyUpdatesLimit = 25;
		int contractedNodes = 0;
		int totalShortcuts = 0;
		boolean lastNode = false;
		int i = 0; // Index of node / node id in contraction order

		while (!contractionQueue.isEmpty()) {
			int candidate = contractionQueue.poll();
			int candidateRank = rank[candidate];
			if (contractionQueue.peek() != null && calculateRank(contractionQueue.peek(), false) < candidateRank) {
				rank[candidate] = calculateRank(candidate, false);
				contractionQueue.add(candidate);
				lazyUpdates++;
				if (lazyUpdates > lazyUpdatesLimit) {
					System.out.println("Updating entire PQ ...");
					contractionQueue = updateAllPriorities();
					lazyUpdates = 0;
				}
			} else {
				if (candidateRank > 50)
					System.out.println(candidateRank);
				rank[candidate] = candidateRank;
				contractionOrder[candidate] = i;
				i++;
				contractedNodes++;
				nodeDegrees += graph.degree(candidate);

				contractNode(candidate, totalShortcuts, lastNode);

				if (contractedNodes % 2000 == 0) {
					averageNodeDegree = nodeDegrees / 2000;
					nodeDegrees = 0;
					System.out.println("Contracted Nodes: " + contractedNodes + " Shortcuts made " + shortcuts.size());
				}
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
			rank[node] = calculateRank(node, true);
			newQueue.add(node);
		}
		return newQueue;
	}

	/**
	 * Initialize contractionQueue with an approximate rank.
	 */
	private void initNodeOrder() {
		IntStream.range(0, graph.V()).forEach(i -> {
			rank[i] = calculateRank(i, false);
			contractionQueue.add(i);
		});
	}

	public int contractNode(int node, int totalShortcuts, boolean lastNode) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<Edge> shortcutsCreated = new HashSet<>();
		int countShortcutsCreated = 0;
		int allowedHops = defineAllowedHops();
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (j.equals(k))
					continue;
				int u = j.other(node);
				int w = k.other(node);
				double sumWeight = j.weight() + k.weight();

				if (!j.visited() && !k.visited()) {
					Edge shortcut = new Edge(u, w, sumWeight, false, true);
					if (shortcutsCreated.contains(shortcut)) {
						continue;
					}

					if (!localSearch.hasWitnessPath(graph, u, w, node, sumWeight, true, allowedHops)) {
						shortcutsCreated.add(shortcut);
						graph.addEdge(shortcut);
						shortcuts.addEdge(u, w);
						countShortcutsCreated++;
					}
				}
				if(shortcutsCreated.size() > 10) break;
			}
		}

		for (Edge e : adjacentEdges) {
			e.visit();
		}

		return countShortcutsCreated;
	}

	public int totalReorderContractSimulation(int node) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<Edge> simulatedShortcuts = new HashSet<>();
		int countShortcutsCreated = 0;
		int allowedHops = defineAllowedHops();
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (j.equals(k))
					continue;
				int u = j.other(node);
				int w = k.other(node);
				double sumWeight = j.weight() + k.weight();

				if (!j.visited() && !k.visited()) {
					Edge shortcut = new Edge(u, w, sumWeight, false, true);
					if (simulatedShortcuts.contains(shortcut)) {
						continue;
					}

					if (countShortcutsCreated < 50
							&& !localSearch.hasWitnessPath(graph, u, w, node, sumWeight, true, allowedHops)) {
						simulatedShortcuts.add(shortcut);
					}
				}
			}
		}
		return simulatedShortcuts.size();
	}

	public int simulateContraction(int node) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<Edge> simulatedShortcuts = new HashSet<>();
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (!k.equals(j)) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					if (!localSearch.hasWitnessPath(graph, u, w, node, sumWeight, true, 1)) {
						Edge shortcut = new Edge(u, w, sumWeight, false, true);
						simulatedShortcuts.add(shortcut);
					}
				}
			}
		}
		return simulatedShortcuts.size();
	}

	/**
	 * We want to allow only 5 hops when contracting the less important nodes, and
	 * allow many hops when contracting the more important nodes.
	 */

	private int defineAllowedHops() {
		int allowedHops = 5;
		if (averageNodeDegree >= 3) {
			allowedHops = 30;
		}
		if (averageNodeDegree >= 4) {
			allowedHops = 50;
		}
		if (averageNodeDegree >= 5) {
			allowedHops = 200;
		}
		return allowedHops;
	}

	/**
	 * Fast local 1-hop search for witness paths. Only used for very first estimation
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

	public int[] getContractionOrder() {
		return contractionOrder;
	}

	public int[] getRank() {
		return rank;
	}
}