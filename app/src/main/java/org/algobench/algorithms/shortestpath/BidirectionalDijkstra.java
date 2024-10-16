package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;

import java.util.Iterator;

public class BidirectionalDijkstra {
	private double[] distL;
	private double[] distR;
	private double d;
	private boolean[] settled;

	private Edge[] edgeTo;
	private IndexMinPQ<Double> pqL;
	private IndexMinPQ<Double> pqR;

	private static int relax;

	public BidirectionalDijkstra(EdgeWeightedGraph graph, int source, int target) {
		Iterator edgeIterator = graph.edges().iterator();

		while (edgeIterator.hasNext()) {
			Edge e = (Edge) edgeIterator.next();
			if (e.weight() < 0.0) {
				throw new IllegalArgumentException("edge " + e + " has negative weight");
			}
		}

		this.distL = new double[graph.V()];
		this.distR = new double[graph.V()];
		this.settled = new boolean[graph.V()];

		this.edgeTo = new Edge[graph.V()];
		this.validateVertex(source);

		int v;
		d = Double.POSITIVE_INFINITY;
		for(v = 0; v < graph.V(); ++v) {
			this.distL[v] = Double.POSITIVE_INFINITY;
			this.distR[v] = Double.POSITIVE_INFINITY;
			this.settled[v] = false;
		}

		this.distL[source] = 0.0;
		this.distR[target] = 0.0;

		// pq stuff
		this.pqL = new IndexMinPQ(graph.V());
		this.pqL.insert(source, this.distL[source]);
		this.pqR = new IndexMinPQ(graph.V());
		this.pqR.insert(target, this.distL[target]);


		while (!this.pqL.isEmpty() || !this.pqR.isEmpty()) {
			boolean right = true;
			if (!this.pqL.isEmpty() && this.pqL.minKey() <= this.pqR.minKey()) {
				right = false;
			}
			int u;
			if (right) {
				u = this.pqR.delMin();

			} else {
				u = this.pqL.delMin();
			}
			if (settled[u]) {
				double maybshortest = this.distL[u] + this.distR[u];
				if (maybshortest > d) {
					break;
				}
			}
			settled[u] = true;

			Iterator adjecentVerticyIterator = graph.adj(u).iterator();

			while (adjecentVerticyIterator.hasNext()) {
				Edge e = (Edge) adjecentVerticyIterator.next();
				relax++;
				this.relax(e, u, right);
			}
			d = Math.min(d, this.pqL.minKey() + this.pqR.minKey());
		}

	}

	private void relax(Edge e, int v, boolean right) {
		int w = e.other(v);
		if (right) {
			if (this.distR[w] > this.distR[v] + e.weight()) {
				this.distR[w] = this.distR[v] + e.weight();
				this.edgeTo[w] = e;
				if (this.pqR.contains(w)) {
					this.pqR.decreaseKey(w, this.distR[w]);
				} else {
					this.pqR.insert(w, this.distR[w]);
				}
			}
		} else if (this.distL[w] > this.distL[v] + e.weight()) {
			this.distL[w] = this.distL[v] + e.weight();
			this.edgeTo[w] = e;
			if (this.pqL.contains(w)) {
				this.pqL.decreaseKey(w, this.distL[w]);
			} else {
				this.pqL.insert(w, this.distL[w]);
			}
		}

	}

	public double distTo(int v) {
		this.validateVertex(v);
		return this.distL[v];
	}

	public boolean hasPathTo(int v) {
		this.validateVertex(v);
		return this.distL[v] < Double.POSITIVE_INFINITY;
	}

	public Iterable<Edge> pathTo(int v) {
		this.validateVertex(v);
		if (!this.hasPathTo(v)) {
			return null;
		} else {
			Stack<Edge> path = new Stack();
			int x = v;

			for(Edge e = this.edgeTo[v]; e != null; e = this.edgeTo[x]) {
				path.push(e);
				x = e.other(x);
			}

			return path;
		}
	}

	private void validateVertex(int v) {
		int V = this.distL.length;
		if (v < 0 || v >= V) {
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
		}
	}
}
