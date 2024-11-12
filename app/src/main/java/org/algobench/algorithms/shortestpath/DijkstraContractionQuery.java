package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.IndexMinPQ;
import org.graalvm.collections.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class DijkstraContractionQuery {
	private double[] distS;
	private double[] distT;
	private double d;
	private boolean[] settledL;
	private boolean[] settledR;

	private Edge[] edgeToL;
	private Edge[] edgeToR;
	private IndexMinPQ<Double> pqUp;
	private IndexMinPQ<Double> pqDown;
	private long countRelaxedEdges;

	public long getCountRelaxedEdges() {
		return countRelaxedEdges;
	}

	public double getShortestPath() {
		return d;
	}

	public DijkstraContractionQuery(EdgeWeightedGraph graph, int source, int target) {
		// Initialize variables
		countRelaxedEdges = 0;
		this.distS = new double[graph.V()];
		this.distT = new double[graph.V()];
		this.settledL = new boolean[graph.V()];
		this.settledR = new boolean[graph.V()];
		this.edgeToL = new Edge[graph.V()];
		this.edgeToR = new Edge[graph.V()];

		Arrays.fill(distS, Double.POSITIVE_INFINITY);
		Arrays.fill(distT, Double.POSITIVE_INFINITY);

		this.distS[source] = 0.0;
		this.distT[target] = 0.0;

		// Initialize priority queues
		this.pqUp = new IndexMinPQ<>(graph.V());
		this.pqUp.insert(source, this.distS[source]);
		this.pqDown = new IndexMinPQ<>(graph.V());
		this.pqDown.insert(target, this.distT[target]);

		d = Double.POSITIVE_INFINITY;

		while (!pqUp.isEmpty() || !pqDown.isEmpty()) {
			if (!pqUp.isEmpty() && !pqDown.isEmpty()) {
				// Check if both directions can be terminated early
				if (d < Math.min(pqUp.minKey(), pqDown.minKey())) {
					break;
				}

				// Choose the direction with the smaller minKey
				if (pqUp.minKey() <= pqDown.minKey()) {
					int u = pqUp.delMin();
					if (settledR[u]) { // Early termination if the vertex is settled in both directions
						d = Math.min(d, distS[u] + distT[u]);
						break;
					}
					settleVertex(graph, u, true);
				} else {
					int u = pqDown.delMin();
					if (settledL[u]) { // Early termination if the vertex is settled in both directions
						d = Math.min(d, distS[u] + distT[u]);
						break;
					}
					settleVertex(graph, u, false);
				}
			} else if (!pqUp.isEmpty()) {
				int u = pqUp.delMin();
				if (settledR[u]) {
					d = Math.min(d, distS[u] + distT[u]);
					break;
				}
				settleVertex(graph, u, true);
			} else if (!pqDown.isEmpty()) {
				int u = pqDown.delMin();
				if (settledL[u]) {
					d = Math.min(d, distS[u] + distT[u]);
					break;
				}
				settleVertex(graph, u, false);
			}
		}
	}

	private void settleVertex(EdgeWeightedGraph graph, int vertex, boolean isUp) {
		if (isUp) {
			settledL[vertex] = true; // Mark vertex as settled for upward direction
			for (Edge e : graph.adj(vertex)) {
				if (graph.getRank(vertex) < graph.getRank(e.other(vertex))) {
					relax(e, vertex, true);
				}
			}
		} else {
			settledR[vertex] = true; // Mark vertex as settled for downward direction
			for (Edge e : graph.adj(vertex)) {
				if (graph.getRank(vertex) < graph.getRank(e.other(vertex))) {
					relax(e, vertex, false);
				}
			}
		}
	}

	private void relax(Edge e, int v, boolean isUp) {
		int w = e.other(v);
		countRelaxedEdges++;
		if (isUp) {
			// Update distance to w if a shorter path is found
			if (this.distS[w] > this.distS[v] + e.weight()) {
				this.distS[w] = this.distS[v] + e.weight(); // Update distance based on edge weight
				this.edgeToL[w] = e;
				if (this.pqUp.contains(w)) {
					this.pqUp.decreaseKey(w, this.distS[w]);
				} else {
					this.pqUp.insert(w, this.distS[w]);
				}
			}
		} else {
			// Update distance to w if a shorter path is found
			if (this.distT[w] > this.distT[v] + e.weight()) {
				this.distT[w] = this.distT[v] + e.weight(); // Update distance based on edge weight
				this.edgeToR[w] = e;
				if (this.pqDown.contains(w)) {
					this.pqDown.decreaseKey(w, this.distT[w]);
				} else {
					this.pqDown.insert(w, this.distT[w]);
				}
			}
		}
	}

	public double distTo(int v) {
		return d;
	}

	public boolean hasPathTo(int v) {
		return distS[v] < Double.POSITIVE_INFINITY;
	}

	public static void main(String[] args) {
		try (FileInputStream fis = new FileInputStream(
				"/Users/mathiasfaberkristiansen/Projects/ITU - new/Applied-algorithms/algobench/denmark_processed.graph")) {
			EdgeWeightedGraph graph = ParseGraphAugmented.parseAugmentedGraph(fis);

			DijkstraBidirectional bd = new DijkstraBidirectional(graph, 1, 100);
			DijkstraContractionQuery path = new DijkstraContractionQuery(graph, 1, 100);
			System.out.println("d: " + bd.distTo(100) + " relaxed:" + bd.getCountRelaxedEdges());
			System.out.println("d: " + path.distTo(100) + " relaxed: " + path.getCountRelaxedEdges());
			System.out.println(path.hasPathTo(1) + "   " + path.hasPathTo(100));

			// int n = 1;
			// Random random = new Random(12345);
			// Pair[] pairs;
			// pairs = new Pair[n];
			// for (int i = 0; i < n; i++) {
			// pairs[i] = Pair.create(random.nextInt(0, graph.V()), random.nextInt(0,
			// graph.V()));
			// }

			// long sumRelaxedEdges = 0;
			// for (int i = 0; i < pairs.length; i++) {
			// int s = (int) pairs[i].getLeft();
			// int t = (int) pairs[i].getRight();
			// long before = System.currentTimeMillis();
			// DijkstraContractionQuery path = new DijkstraContractionQuery(graph, s, t);
			// long after = System.currentTimeMillis();
			// sumRelaxedEdges = path.getCountRelaxedEdges();
			// System.out.println("Relaxed: " + sumRelaxedEdges + " relaxed edges in time: "
			// + ((after - before)) + " " + path.d);
			// sumRelaxedEdges = 0;
			// }
			// long after = System.currentTimeMillis();
			// System.out.println("Time taken: " + ((after - before)) + "ms per (s,t)
			// search");
			// System.out.println("Relaxed: " + sumRelaxedEdges + " relaxed edges");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
