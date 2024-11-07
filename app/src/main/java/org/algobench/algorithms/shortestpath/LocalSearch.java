package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.IndexMinPQ;

public class LocalSearch {
    private double[] distTo;
    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;
    private final EdgeWeightedGraph graph;
    private int[] epoch;
    private int currentEpoch;

    public LocalSearch(EdgeWeightedGraph graph) {
        this.graph = graph;
        this.epoch = new int[graph.V()];
        init();
    }

    public boolean hasWitnessPath(int[] ranks, int source, int target, int excluded, double sumWeight) {
        this.distTo[source] = 0.0;
        currentEpoch++;

        if (pq.contains(source)) {
            this.pq.changeKey(source, this.distTo[source]);
        } else {
            this.pq.insert(source, this.distTo[source]);
        }

        int maxSettledNodes = 50;
        while (!this.pq.isEmpty() && maxSettledNodes > 0) {
            int v = this.pq.delMin();

            if (distTo(v) > sumWeight) {
                break;
            }

	        for (Edge e : graph.adj(v)) {
		        if (ranks[e.other(v)] > ranks[source] && e.other(v) != excluded) {
			        this.relax(e, v);
		        }
	        }
            maxSettledNodes--;
        }

        return distTo[target] > sumWeight;
    }

    private void init() {

	    for (Edge e : graph.edges()) {
		    if (e.weight() < 0.0) {
			    throw new IllegalArgumentException("edge " + e + " has negative weight");
		    }
	    }

        this.distTo = new double[graph.V()];
        this.edgeTo = new Edge[graph.V()];

        for (int v = 0; v < graph.V(); ++v) {
            this.distTo[v] = Double.POSITIVE_INFINITY;
        }

        this.pq = new IndexMinPQ<>(graph.V());
    }

    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (this.epoch[w] != currentEpoch) {
            distTo[w] = Double.POSITIVE_INFINITY;
            epoch[w] = currentEpoch;
        }
        if (this.distTo[w] > this.distTo[v] + e.weight()) {
            this.distTo[w] = this.distTo[v] + e.weight();
            this.edgeTo[w] = e;
            if (this.pq.contains(w)) {
                this.pq.decreaseKey(w, this.distTo[w]);
            } else {
                this.pq.insert(w, this.distTo[w]);
            }
        }
    }

    public void emptyQueue() {
        while (!this.pq.isEmpty()) {
            pq.delMin();
        }
    }

    public double distTo(int v) {
        this.validateVertex(v);
        return this.distTo[v];
    }

    private void validateVertex(int v) {
        int V = this.distTo.length;
        if (v < 0 || v >= V) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
        }
    }
}
