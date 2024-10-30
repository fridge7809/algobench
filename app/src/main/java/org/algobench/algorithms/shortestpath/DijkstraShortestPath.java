package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;

import java.util.Iterator;

public class DijkstraShortestPath {
	private double[] distTo;
	private Edge[] edgeTo;
	private IndexMinPQ<Double> pq;
	private static int countRelaxed;

	public int getRelaxed() {
		return countRelaxed;
	}
	public DijkstraShortestPath(EdgeWeightedGraph graph, int source, int target) {
		Iterator edgeIterator = graph.edges().iterator();

		while (edgeIterator.hasNext()) {
			Edge e = (Edge) edgeIterator.next();
			if (e.weight() < 0.0) {
				throw new IllegalArgumentException("edge " + e + " has negative weight");
			}
		}

		this.distTo = new double[graph.V()];
		this.edgeTo = new Edge[graph.V()];
		this.validateVertex(source);

		int v;
		for (v = 0; v < graph.V(); ++v) {
			this.distTo[v] = Double.POSITIVE_INFINITY;
		}

		this.distTo[source] = 0.0;
		this.pq = new IndexMinPQ(graph.V());
		this.pq.insert(source, this.distTo[source]);

		while (!this.pq.isEmpty()) {
			v = this.pq.delMin();
			if (v == target) {
				break;
			}
			Iterator adjecentVerticyIterator = graph.adj(v).iterator();

			while (adjecentVerticyIterator.hasNext()) {
				Edge e = (Edge) adjecentVerticyIterator.next();
				countRelaxed++;
				this.relax(e, v);
			}
		}

	}

	private void relax(Edge e, int v) {
		int w = e.other(v);
		if (this.distTo[w] > this.distTo[v] + e.weight()) {
			countRelaxed++;
			this.distTo[w] = this.distTo[v] + e.weight();
			this.edgeTo[w] = e;
			if (this.pq.contains(w)) {
				this.pq.decreaseKey(w, this.distTo[w]);
			} else {
				this.pq.insert(w, this.distTo[w]);
			}
		}

	}

	public double distTo(int v) {
		this.validateVertex(v);
		return this.distTo[v];
	}

	public boolean hasPathTo(int v) {
		this.validateVertex(v);
		return this.distTo[v] < Double.POSITIVE_INFINITY;
	}

	public Iterable<Edge> pathTo(int v) {
		this.validateVertex(v);
		if (!this.hasPathTo(v)) {
			return null;
		} else {
			Stack<Edge> path = new Stack();
			int x = v;

			for (Edge e = this.edgeTo[v]; e != null; e = this.edgeTo[x]) {
				path.push(e);
				x = e.other(x);
			}

			return path;
		}
	}

	private void validateVertex(int v) {
		int V = this.distTo.length;
		if (v < 0 || v >= V) {
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
		}
	}
}
