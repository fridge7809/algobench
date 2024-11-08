package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.HashSet;
import java.util.Set;

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

    public boolean hasWitnessPath(EdgeWeightedGraph graph, int source, int target, int excluded, double sumWeight) {
        int settledCount = 0;

        this.distTo[source] = 0.0;
        currentEpoch++;

        this.pq.insert(source, 0.0);

        while (!this.pq.isEmpty() && settledCount < 5) {
            int v = this.pq.delMin();
            settledCount++;

            if (source == excluded) {
                throw new IllegalArgumentException("Source excluded");
            }

            if (distTo(v) > sumWeight) {
                break;
            }

	        for (Edge e : graph.adj(v)) {
		        if (e.other(v) != excluded) {
			        this.relax(e, v);
		        }
	        }
        }

        emptyQueue();
        return distTo[target] <= sumWeight;
    }

    private void init() {
        this.distTo = new double[graph.V()];
        this.edgeTo = new Edge[graph.V()];

        for (int v = 0; v < graph.V(); v++) {
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
        return this.distTo[v];
    }

    public int[] getEpoch() {
        return this.epoch;
    }

    public double[] getDistoTo() {
        return this.distTo;
    }
}
