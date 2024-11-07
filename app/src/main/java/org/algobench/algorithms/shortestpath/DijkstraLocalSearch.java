package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DijkstraLocalSearch {
    private double[] distTo;
    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;
    private EdgeWeightedGraph graph;
    Set<Integer> visitedV;

    public DijkstraLocalSearch(EdgeWeightedGraph graph) {
        this.graph = graph;
        initDijkstra();
    }

    public void searchGraph(Map<Integer, Integer> ranks, int source, int excluded, double sumWeight, Boolean isSim) {
        this.distTo[source] = 0.0;

        // use this when isSim = true, to reset relaxed edges/vertices in the distto
        visitedV = new HashSet<>();

        if (pq.contains(source)) {
            this.pq.changeKey(source, this.distTo[source]);
        } else {
            this.pq.insert(source, this.distTo[source]);
        }

        int oneHopStop = 0;
        while (!this.pq.isEmpty() && oneHopStop < 50) {
            int v = this.pq.delMin();
            if (distTo(v) > sumWeight) {
                break;
            }

            Iterator<Edge> adjecentVerticyIterator = graph.adj(v).iterator();
            while (adjecentVerticyIterator.hasNext()) {
                Edge e = (Edge) adjecentVerticyIterator.next();
                if (isSim) {
                    // System.out.println("Simulating relax: " + e +" V:" + v);
                    this.relax(e, v); // simulate relaxation
                } else if (ranks.get(e.other(v)) > ranks.get(source) && e.other(v) != excluded) {
                    // countRelaxed++;
                    this.relax(e, v);
                }
            }
            oneHopStop++;
        }
        // System.out.println(countRelaxed + " countrelaxed");
        // if(isSim) {
        // resetDistToValues(visitedV);
        // this.pq = new IndexMinPQ<>(graph.V());
        // }
    }

    public void resetDistToValues() {
        for (int v : visitedV) {
            this.distTo[v] = Double.POSITIVE_INFINITY;
        }

        while (!pq.isEmpty()) {
            pq.delMin();
        }

        visitedV = new HashSet<>();
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

        this.pq = new IndexMinPQ<>(graph.V());
    }

    private void relax(Edge e, int v) {
        int w = e.other(v);
        visitedV.add(w);
        if (this.distTo[w] > this.distTo[v] + e.weight()) {
            this.distTo[w] = this.distTo[v] + e.weight();
            this.edgeTo[w] = e; // OBS: edgeTo is NOT reset when using simulateDijkstra. Could cause problems,
                                // but is currently only used for pathTo func
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
