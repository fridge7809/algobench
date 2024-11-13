package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.Arrays;

public class LocalSearch {
    private double[] distTo;
    private IndexMinPQ<Double> pq;
    private long countRelaxedEdges;

    private final EdgeWeightedGraph graph;
    private int[] epoch;
    private int currentEpoch;

    public LocalSearch(EdgeWeightedGraph graph) {
        this.graph = graph;
        this.epoch = new int[graph.V()];
        init();
    }

    private void init() {
        countRelaxedEdges = 0;
        this.distTo = new double[graph.V()];
        this.pq = new IndexMinPQ<>(graph.V());

        Arrays.fill(distTo, Double.POSITIVE_INFINITY);
    }

    public boolean hasWitnessPath(EdgeWeightedGraph graph, int source, int target, int excluded, double sumWeight,
            boolean limitSettledNodes, int allowedHops) {
        emptyQueue(); // Should be empty, but clearing the queue to be sure
        int settledCount = 0;
        this.distTo[source] = 0.0;

        currentEpoch++;
        this.pq.insert(source, 0.0);
        this.epoch[source] = currentEpoch;

        int hops = 0;
        double currentShortestPathToTarget = Double.POSITIVE_INFINITY;

        while (!this.pq.isEmpty() && hops < allowedHops) {
            int v = this.pq.delMin();
            if(v == target) break; // early stopping
            
            if (limitSettledNodes)
                settledCount++; // only increments if we want to limit settled nodes

            if (settledCount > 100)
                break; //break? There lies a bug in here, which causes this to not make shortcuts in some cases where they are needed

            // Relax all edges for the current node, excluding edges to the 'excluded' node
            // and not using edges that are already visited
            for (Edge e : graph.adj(v)) {
                int w = e.other(v);
                if (w != excluded && !e.visited()) {
                    this.relax(e, v);
                }
            }
            // updates currentshortest path, if distTo target is found and lower thancurrent
            if (distTo(target) < currentShortestPathToTarget)
                currentShortestPathToTarget = distTo(target);

            // checks whether we found a path to target lower than sumweight AND makes sure
            // that the distTo value for the current vertex we are checking are larger
            // than sumWeight (early stopping)
            if (currentShortestPathToTarget < sumWeight) {
                return true;
            }

            if (currentShortestPathToTarget > sumWeight && distTo(v) > sumWeight) {
                break;
            }
            hops++;
        }
        return distTo(target) <= sumWeight;
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
            // this.edgeTo[w] = edgeTo[w] = e;
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
