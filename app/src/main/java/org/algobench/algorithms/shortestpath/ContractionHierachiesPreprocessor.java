package org.algobench.algorithms.shortestpath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.PriorityQueue;

public class ContractionHierachiesPreprocessor {

	private final EdgeWeightedGraph graph;
	private DijkstraLocalSearch dijkstraLocalSearch;
	private DijkstraLocalSearch dijkstraInit;
	private final Set<Edge> shortcuts;
	private PriorityQueue<Integer> contractionQueue;
	private final int[] deletedNeighbors;
	private Set<Edge> visitedInit;
	int shortcutsAdded;

	public ContractionHierachiesPreprocessor(EdgeWeightedGraph graph) {
		this.graph = graph;
		this.shortcuts = new HashSet<>();
		this.deletedNeighbors = new int[graph.V()];
		this.dijkstraLocalSearch = new DijkstraLocalSearch(graph);
		this.visitedInit = new HashSet<>();
		this.dijkstraInit = new DijkstraLocalSearch(graph);
		shortcutsAdded = 0;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraph.parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
			ContractionHierachiesPreprocessor ch = new ContractionHierachiesPreprocessor(graph);
			ch.preprocess();
			System.out.println(ch.shortcuts.size());
			StringBuilder sb = new StringBuilder();
			sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");
			for (int v = 0; v < ch.graph.V(); v++) {
				sb.append(v).append(" ").append(ch.graph.getRank(v)).append("\n");
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

	public int rank(int node) {
		return contract(node, true) + deletedNeighbors[node];
	}

	public int edgeDiff(int node) {
		int deg = graph.degree(node);
		return (deg * deg) - deg;
	}

	public int preprocess() {
		long before = System.nanoTime();

		contractionQueue = new PriorityQueue<>((a, b) -> Integer.compare(rank(a), rank(b)));
		for (int v = 0; v < graph.V(); v++) {
			contractionQueue.add(v);
			graph.setRank(v, rank(v));
			before = printProgress("Init contraction PQ: ", v, before);
		}
		int contractedNodes = 0;
		while (!contractionQueue.isEmpty()) {
			int v = contractionQueue.poll();
			int rankCurrent = rank(v);
			if (contractionQueue.peek() != null && rank(contractionQueue.peek()) < rankCurrent) {
				contractionQueue.add(v);
				int i = 0;
			} else {
				graph.setRank(v, rankCurrent);
				shortcutsAdded += contract(v, false);
				for (Edge edge : graph.adj(v)) {
					deletedNeighbors[edge.other(v)]++;
				}
				contractedNodes++;
				before = printProgress("Contraction progress: ", contractedNodes, before);
			}
		}
		return contractedNodes;
	}

	private static long printProgress(String msg, int contractedNodes, long before) {
		if (contractedNodes % 10000 == 0) {
			long after = System.nanoTime();
			System.out.println(msg + contractedNodes + " in time in seconds: " + (after - before) / 1_000_000_000);
			before = System.nanoTime();
		}
		return before;
	}

	public int contract(int node, boolean sim) {
		int shortcutsAdded = 0;
		List<Edge> adjacentEdges = new ArrayList<Edge>();
		for (Edge e : graph.adj(node)) {
			adjacentEdges.add(e);
		}
		for (Edge j : adjacentEdges) {
			for (Edge k : adjacentEdges) {
				if (k != j) {
					int u = j.other(node);
					int w = k.other(node);
					double sumWeight = j.weight() + k.weight();

					if (dijkstraLocalSearch.distTo(w) > sumWeight) {
						shortcutsAdded++;
						if (!sim) {
							shortcuts.add(new Edge(u, w, sumWeight, true));
							graph.addEdge(new Edge(u, w, sumWeight, true));
						}
					}
				}
			}
		}
		return shortcutsAdded;
	}

}
