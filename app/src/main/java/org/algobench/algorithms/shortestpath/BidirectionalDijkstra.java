package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;

public class BidirectionalDijkstra {
    private double[] distL;
    private double[] distR;
    private double d;
    private boolean[] settledL;
    private boolean[] settledR;

    private Edge[] edgeToL;
    private Edge[] edgeToR;
    private IndexMinPQ<Double> pqL;
    private IndexMinPQ<Double> pqR;
    private int countRelaxedEdges;

    public int getCountRelaxedEdges() {
        return countRelaxedEdges;
    }

    public double getShortestPath() {
        return d;
    }

    public BidirectionalDijkstra(EdgeWeightedGraph graph, int source, int target) {
        // Initialize variables
        countRelaxedEdges = 0;
        this.distL = new double[graph.V()];
        this.distR = new double[graph.V()];
        this.settledL = new boolean[graph.V()];
        this.settledR = new boolean[graph.V()];
        this.edgeToL = new Edge[graph.V()];
        this.edgeToR = new Edge[graph.V()];

        for (int v = 0; v < graph.V(); v++) {
            this.distL[v] = Double.POSITIVE_INFINITY;
            this.distR[v] = Double.POSITIVE_INFINITY;
        }

        this.distL[source] = 0.0;
        this.distR[target] = 0.0;

        // Initialize priority queues
        this.pqL = new IndexMinPQ<>(graph.V());
        this.pqL.insert(source, this.distL[source]);
        this.pqR = new IndexMinPQ<>(graph.V());
        this.pqR.insert(target, this.distR[target]);

        d = Double.POSITIVE_INFINITY; // Initialize shortest distance tracker

        while (!this.pqL.isEmpty() && !this.pqR.isEmpty()) {
            // Early stopping criteria: stop searching when the path about to be searched is
            // longer than the current shortest path
            if (pqL.minKey() + pqR.minKey() >= d) {
                System.out.println(pqL.minKey() + " - " + pqR.minKey() + " - " + d);
                break;
            }

            // Process the vertex with the minimum distance from left
            int u = this.pqL.minIndex();
            if (this.pqR.contains(u)) { // Check if the vertex is settled from right
                d = Math.min(d, this.distL[u] + this.distR[u]);
            }
            this.pqL.delMin();
            settleVertex(graph, u, true);

            // Process the vertex with the minimum distance from right
            int v = this.pqR.minIndex();
            if (this.pqL.contains(v)) { // Check if the vertex is settled from left
                d = Math.min(d, this.distL[v] + this.distR[v]);
            }
            this.pqR.delMin();
            settleVertex(graph, v, false);

        }
        System.out.println(d); // shortest path :D
    }

    private void settleVertex(EdgeWeightedGraph graph, int vertex, boolean isLeft) {
        if (isLeft) {
            if (settledL[vertex])
                return; // Already settled
            settledL[vertex] = true;

            for (Edge e : graph.adj(vertex)) {
                relax(e, vertex, true);
            }
        } else {
            if (settledR[vertex])
                return; // Already settled
            settledR[vertex] = true;

            for (Edge e : graph.adj(vertex)) {
                relax(e, vertex, false);
            }
        }
    }

    private void relax(Edge e, int v, boolean isLeft) {
        int w = e.other(v);
        countRelaxedEdges++;
        if (isLeft) {
            // Update distance to w if a shorter path is found
            if (this.distL[w] > this.distL[v] + e.weight()) {
                this.distL[w] = this.distL[v] + e.weight(); // Update distance based on edge weight
                this.edgeToL[w] = e;
                if (this.pqL.contains(w)) {
                    this.pqL.decreaseKey(w, this.distL[w]);
                } else {
                    this.pqL.insert(w, this.distL[w]);
                }
            }
        } else {
            // Update distance to w if a shorter path is found
            if (this.distR[w] > this.distR[v] + e.weight()) {
                this.distR[w] = this.distR[v] + e.weight(); // Update distance based on edge weight
                this.edgeToR[w] = e;
                if (this.pqR.contains(w)) {
                    this.pqR.decreaseKey(w, this.distR[w]);
                } else {
                    this.pqR.insert(w, this.distR[w]);
                }
            }
        }
    }

    public double distTo(int v) {
        return this.distL[v];
    }

    public boolean hasPathTo(int v) {
        return this.distL[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v) {
        if (!hasPathTo(v))
            return null;
        Stack<Edge> path = new Stack<>();
        for (Edge e = edgeToL[v]; e != null; e = edgeToL[e.either()]) {
            path.push(e);
        }
        return path;
    }
}