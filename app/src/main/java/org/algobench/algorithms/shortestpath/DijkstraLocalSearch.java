package org.algobench.algorithms.shortestpath;


import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.Iterator;

public class DijkstraLocalSearch {
    private double[] distTo;
    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;
    private EdgeWeightedGraph graph;

    public DijkstraLocalSearch(EdgeWeightedGraph graph) {
        this.graph = graph;
        initDijkstra();
    }

    public int searchGraph(int source, int excluded, double sumWeight) {
        int costOfSearch = 0;
        this.distTo[source] = 0.0;

        if (pq.contains(source)) {
            this.pq.changeKey(source, this.distTo[source]);
        } else {
            this.pq.insert(source, this.distTo[source]);
        }

        int oneHopStop = 1;
        while (!this.pq.isEmpty() && oneHopStop != 0) {
            int v = this.pq.delMin();
            if (distTo(v) > sumWeight) {
                break;
            }
            Iterator<Edge> adjecentVerticyIterator = graph.adj(v).iterator();

            while (adjecentVerticyIterator.hasNext()) {
                costOfSearch++;
                Edge e = adjecentVerticyIterator.next();
                int other = e.other(v);
                if (graph.getRank(other) > graph.getRank(source) && e.other(v) != excluded) {
                    this.relax(e, v);
                }
            }
            oneHopStop--;
        }
        return costOfSearch;
    }

    private void initDijkstra() {
        Iterator<Edge> edgeIterator = graph.edges().iterator();

        while (edgeIterator.hasNext()) {
            Edge e = edgeIterator.next();
            if (e.weight() < 0.0) {
                throw new IllegalArgumentException("edge " + e + " has negative weight");
            }
        }

        this.distTo = new double[graph.V()];
        this.edgeTo = new Edge[graph.V()];

        int v;
        for (v = 0; v < graph.V(); ++v) {
            this.distTo[v] = Double.POSITIVE_INFINITY;
        }

        this.pq = new IndexMinPQ(graph.V());
    }

    private void relax(Edge e, int v) {
        int w = e.other(v);
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
