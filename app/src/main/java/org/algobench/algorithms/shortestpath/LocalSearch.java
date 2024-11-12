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

    public boolean hasWitnessPath(EdgeWeightedGraph graph, int source, int target, int excluded, double sumWeight, boolean limitSettledNodes, int allowedHops) {
        emptyQueue(); // clear the queue
        int settledCount = 0;
        this.distTo[source] = 0.0;
        double shortestPathToTarget = Double.POSITIVE_INFINITY;

        currentEpoch++;
        this.pq.insert(source, 0.0);
        this.epoch[source] = currentEpoch;

        int hops = 0;
        double currentShortestPathToTarget = Double.POSITIVE_INFINITY;

        /**
         * Note: 
         * We only want to have a settled nodes limit on the preliminary ranking stage, and no limit on actual contraction
         * TODO: implement staged hop limits
         */
        while (!this.pq.isEmpty() && hops < allowedHops) {
            int v = this.pq.delMin();
            if(limitSettledNodes) settledCount++; // only increments if we want to limit settled nodes
            if(settledCount>100) return true;

            // Relax all edges for the current node, excluding edges to the 'excluded' node
            // and not using edges that are already visited
            for (Edge e : graph.adj(v)) {
                int w = e.other(v);
                if (w != excluded && !e.visited()) {
                    this.relax(e, v);
                }
            }

            // updates currentshortest path, if distTo target is found and lower than
            // current
            if (distTo(target) < currentShortestPathToTarget)
                currentShortestPathToTarget = distTo(target);

            // checks whether we found a path to target lower than sumweight AND makes sure
            // that the distTo value for the current vertex we are checking are larger than
            // sumWeight (early stopping)
            if (currentShortestPathToTarget < sumWeight) {
                return true;
            }

            if (currentShortestPathToTarget > sumWeight && distTo(v) > sumWeight) {
                return false; // maybe??
            }

            // && distTo(v) > sumWeight?

            hops++;
        }
        // System.out.println("Relaxed edges: " + countRelaxed);
        return shortestPathToTarget <= sumWeight;
    }

    private void init() {
        this.distTo = new double[graph.V()];
        this.edgeTo = new Edge[graph.V()];

        for (int v = 0; v < graph.V(); v++) {
            this.distTo[v] = Double.POSITIVE_INFINITY;
            this.epoch[v] = 1;
        }

        this.pq = new IndexMinPQ<>(graph.V());
    }

    private void relax(Edge e, int v) {
        int w = e.other(v);
        if (epoch[w] != currentEpoch) {
            distTo[w] = Double.POSITIVE_INFINITY;
            epoch[w] = currentEpoch;
        }
        if (epoch[v] != currentEpoch) {
            distTo[v] = Double.POSITIVE_INFINITY;
            epoch[v] = currentEpoch;
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
        if (epoch[v] != currentEpoch) {
            distTo[v] = Double.POSITIVE_INFINITY;
            epoch[v] = currentEpoch;
        }
        return this.distTo[v];
    }

    public double[] getDistoTo() {
        return this.distTo;
    }
}
