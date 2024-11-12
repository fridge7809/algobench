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

	private Edge[] edgeToL;
	private Edge[] edgeToR;
	private IndexMinPQ<Double> pqUp;
	private IndexMinPQ<Double> pqDown;
	private long countRelaxedEdges;
	private int[] contractionOrder;

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

		contractionOrder = graph.getRanks();

		d = Double.POSITIVE_INFINITY;

		boolean rDirection = true; // true = up, false = down

		while ((!this.pqUp.isEmpty() || !this.pqDown.isEmpty())) {

			// Ensure pqUp and pqDown are non-empty before calling minKey()
			if (!this.pqUp.isEmpty() && !this.pqDown.isEmpty()) {
				if (d < Math.min(pqUp.minKey(), pqDown.minKey())) {
					break;
				}
			} else if (!this.pqUp.isEmpty() && d < pqUp.minKey()) {
				break;
			} else if (!this.pqDown.isEmpty() && d < pqDown.minKey()) {
				break;
			}

			if (!this.pqUp.isEmpty()) {
				rDirection = !rDirection;
				int u = this.pqUp.delMin();
				d = Math.min(d, this.distS[u] + this.distT[u]);
				settleVertex(graph, u, !rDirection);
			}

			if (!this.pqDown.isEmpty()) {
				rDirection = !rDirection;
				int u = this.pqDown.delMin();
				d = Math.min(d, this.distS[u] + this.distT[u]);
				settleVertex(graph, u, !rDirection);
			}

		}
	}

	private void settleVertex(EdgeWeightedGraph graph, int vertex, boolean isUp) {
		if (isUp) {
			for (Edge e : graph.adj(vertex)) {
				if (contractionOrder[vertex] < contractionOrder[e.other(vertex)]) {
					relax(e, vertex, true);
				}
			}
		} else {
			for (Edge e : graph.adj(vertex)) {
				if (contractionOrder[vertex] < contractionOrder[e.other(vertex)]) {
					relax(e, vertex, false);
				}
			}
		}
	}

	private void relax(Edge e, int v, boolean isLeft) {
		int w = e.other(v);
		countRelaxedEdges++;
		if (isLeft) {
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

	
			int n = 10;
			Random random = new Random(12345);
			Pair[] pairs;
			pairs = new Pair[n];
			for (int i = 0; i < n; i++) {
				pairs[i] = Pair.create(random.nextInt(0, graph.V()), random.nextInt(0,
						graph.V()));
			}

			long sumRelaxedEdges1 = 0;
			long sumRelaxedEdges2 = 0;
			int averageTimeFaster = 0;
			for (int i = 0; i < pairs.length; i++) {
				int s = (int) pairs[i].getLeft();
				int t = (int) pairs[i].getRight();
				long before1 = System.currentTimeMillis();
				DijkstraBidirectional bd = new DijkstraBidirectional(graph, s, t);
				long after1 = System.currentTimeMillis();
				long before2 = System.currentTimeMillis();
				DijkstraContractionQuery path = new DijkstraContractionQuery(graph, s, t);
				long after2 = System.currentTimeMillis();
				sumRelaxedEdges1 = bd.getCountRelaxedEdges();
				sumRelaxedEdges2 = path.getCountRelaxedEdges();
				System.out.println("bd - Relaxed: " + sumRelaxedEdges1 + " relaxed edges in time: "
						+ ((after1 - before1)) + " --------------->  " + bd.distTo(t));
				System.out.println("ch - Relaxed: " + sumRelaxedEdges2 + " relaxed edges in time: "
						+ ((after2 - before2)) + " --------------->  " + path.distTo(t));
				averageTimeFaster += sumRelaxedEdges1/sumRelaxedEdges2;
				System.out.println(sumRelaxedEdges1/sumRelaxedEdges2);
				sumRelaxedEdges1 = 0;
				sumRelaxedEdges2 = 0;
			}
			System.out.println(averageTimeFaster/10);
			// long after = System.currentTimeMillis();
			// System.out.println("Time taken: " + ((after - before)) + "ms per (s,t)
			// search");
			// System.out.println("Relaxed: " + sumRelaxedEdges + " relaxed edges");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
