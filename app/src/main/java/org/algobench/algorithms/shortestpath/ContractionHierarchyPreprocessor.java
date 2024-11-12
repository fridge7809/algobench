package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.Bag;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class ContractionHierarchyPreprocessor {

	// private final EdgeContainer visitedEdges;
	private final EdgeWeightedGraph graph;
	private final LocalSearch localSearch;
	private int[] rank;
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
		// this.visitedEdges = new EdgeContainer();
		this.localSearch = new LocalSearch(graph);
		this.contractionOrder = new ArrayList<>(graph.V());
		this.contractionQueue = new PriorityQueue<>(graph.V(), new NodeComparator());
		this.nodeComparator = new NodeComparator();
	}

	/**
	 * status: kører for længe, men virker måske??
	 * få visualizer til at virke
	 * debug på epoch?
	 */

	public static void main(String[] args) {
		writeAugmentedGraphToFile("app/src/test/resources/denmark.graph", "denmark_processed.graph");
	}

	public static void writeAugmentedGraphToFile(String inputGraphPath, String outputGraphPath) {
		try (FileInputStream fis = new FileInputStream(inputGraphPath);
				FileWriter fw = new FileWriter(outputGraphPath)) {
			EdgeWeightedGraph graph = ParseGraph.parseGraph(fis);
			ContractionHierarchyPreprocessor ch = new ContractionHierarchyPreprocessor(graph);
			ch.preprocess();
			// System.out.println(ch.shortcuts.size());

			// Format augmented graph
			StringBuilder sb = new StringBuilder();
			sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");

			for (int i = 0; i < ch.graph.V(); i++) {
				sb.append(i).append(" ").append(graph.getCoords().get(i).getLeft()).append(" ")
						.append(graph.getCoords().get(i).getRight()).append(" ").append(ch.rank[i]).append("\n");
			}
			for (Edge e : ch.graph.edges()) {
				String[] fromto = e.toString().split("-");
				Long from = Long.parseLong(fromto[0]);
				Long to = Long.parseLong(fromto[1].split(" ")[0]);
				Long isShortcut = Long.parseLong(e.isShortcut() ? "1" : "-1"); //"contracted"
				int weight = (int)e.weight();

				sb.append(from).append(" ").append(to).append(" ").append(weight).append(" ").append(isShortcut).append("\n");
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
	 * Creates an initial node contraction ordering, then begins contraction
	 * process.
	 * Lazy updates rank as it is contracting.
	 */
	public int preprocess() {
		long startTime = System.currentTimeMillis();
		System.out.println("Begin contraction");
		initNodeOrder();
		int lazyUpdates = 0;
		int lazyUpdatesLimit = 20;
		int contractedNodes = 0;
		int totalShortcuts = 0;
		boolean lastNode = false;

		while (!contractionQueue.isEmpty()) {
			int candidate = contractionQueue.poll();
			int candidateRank = rank[candidate];
			if (contractionQueue.peek() != null && calculateRank(contractionQueue.peek(), false) < candidateRank) {
				rank[candidate] = calculateRank(candidate, false);
				contractionQueue.add(candidate);
				lazyUpdates++;
				System.out.println("lazyupdates: " + lazyUpdates);
				System.out.println("pqsize: " + contractionQueue.size());

				if (lazyUpdates > lazyUpdatesLimit) {
					System.out.println("Updating entire PQ ...");
					contractionQueue = updateAllPriorities();
					lazyUpdates = 0;
					System.out.println("Update done!!");
				}
			} else {
				if (contractionQueue.peek() == null) {
					lastNode = true;
				}
				rank[candidate] = candidateRank;
				contractionOrder.add(candidate);
				contractedNodes++;
				long starttime2 = System.currentTimeMillis();

				contractNode(candidate, totalShortcuts, lastNode);
				long endTime2 = System.currentTimeMillis();
				if (contractedNodes > 566000 && contractedNodes % 100 == 0) {
					System.out.println(
							"(every100) contracting 1 node here takes: " + (endTime2 - starttime2) + " pqsize: "
									+ contractionQueue.size() + " degree of currentNode: " + graph.degree(candidate));
				}
				// totalShortcuts += shortcutsMade;
				if (contractedNodes % 2000 == 0)
					System.out.println("Contracted Nodes: " + contractedNodes + " Shortcuts made "
							+ " for node: " + candidate
							+ " | Total shortcuts: " + shortcuts.size());
				signalDeletionToNeighbours(candidate);
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Contraction time total: " + (endTime - startTime) / 1000.0D + " seconds.");
		System.out.println("Shortcuts added: " + shortcuts.size());
		// System.out.println(shortcuts.getAllEdges());
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
		// if (lastNode) {
		// int count = 0;
		// int countShortcuts = 0;
		// for (Edge e : adjacentEdges) {
		// if (e.isShortcut()) {
		// System.out.println(e.toString());
		// countShortcuts++;
		// }
		// count++;
		// }
		// System.out.println("Node " + node + " has " + count + " edges" + " and " +
		// countShortcuts + " shortcuts");
		// }

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

					if (countShortcutsCreated < 50 && !localSearch.hasWitnessPath(graph, u, w, node, sumWeight)) {
						shortcutsCreated.add(shortcut);
						graph.addEdge(shortcut);
						shortcuts.addEdge(u, w);
						countShortcutsCreated++;
					}
				}
			}
		}
		// if(lastNode) {
		// System.out.println("Contracting node: " + node + " added " +
		// countShortcutsCreated + " shortcuts");
		// }
		for (Edge e : adjacentEdges) {
			e.visit();
		}

		// System.out.println(shortcuts.getAllEdges().toString());
		return countShortcutsCreated;
	}

	/**
	 * An approximation for the number of shortcuts to add when contracting a node.
	 * Uses Fast 1-hop local search to find witness paths.
	 */

	public int totalReorderContractSimulation(int node) {
		Bag<Edge> adjacentEdges = graph.getAdjacentEdges(node);
		Set<Edge> simulatedShortcuts = new HashSet<>();
		int countShortcutsCreated = 0;
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

					if (countShortcutsCreated < 50 && !localSearch.hasWitnessPath(graph, u, w, node, sumWeight)) {
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

					if (!hasWitnessPath(u, w, sumWeight)) {
						Edge shortcut = new Edge(u, w, sumWeight, false, true);
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