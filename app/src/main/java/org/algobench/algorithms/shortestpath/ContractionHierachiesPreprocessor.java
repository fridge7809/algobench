package org.algobench.algorithms.shortestpath;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.PriorityQueue;

public class ContractionHierachiesPreprocessor {

    private EdgeWeightedGraph graph;
    private DijkstraLocalSearch dijkstraLocalSearch;
    private Map<Integer, Integer> ranks;
    private Set<Edge> shortcuts;
    private PriorityQueue<Integer> pq;
    private int[] deletedNeighbors;

    public ContractionHierachiesPreprocessor(EdgeWeightedGraph graph) {
        this.graph = graph;
        ranks = new HashMap<>();
        shortcuts = new HashSet<>();
        deletedNeighbors = new int[graph.V()];
        orderNodeByImportance();
    }

    public Set<Edge> getShortcuts() {
        return shortcuts;
    }

    public int rank(int node) {
        return edgeDifference(node) + deletedNeighbors[node];
    }

    public int edgeDifference(int node) {
        int deg = graph.degree(node);
        return (deg * deg) - deg;
    }

    private void orderNodeByImportance() {
        long before = System.nanoTime();

        dijkstraLocalSearch = new DijkstraLocalSearch(graph);
        pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(rank(a), rank(b)));
        for (int v = 0; v < graph.V(); v++) {
            pq.add(v);
            ranks.put(v, rank(v));
        }
        int scCount = 0;
        while (!pq.isEmpty()) {
            int v = pq.poll();
            int rank = rank(v);
            if (pq.peek() != null && rank(pq.peek()) < rank) {
                pq.add(v);
            } else {
                ranks.put(v, rank);
                contract(v);
                for (Edge edge : graph.adj(v)) {
                    deletedNeighbors[edge.other(v)]++;
                }
                scCount++;
                if (scCount % 2000 == 0) {
                    long after = System.nanoTime();
                    System.out.println(scCount + " in time in seconds: " + (after - before) / 1_000_000_000);
                    before = System.nanoTime();
                }
            }
        }
    }

    public void contract(int node) {
        List<Edge> adjacentEdges = new ArrayList<Edge>();
        for (Edge e : graph.adj(node)) {
            adjacentEdges.add(e);
        }
        for (Edge j : adjacentEdges) {
            for (Edge k : adjacentEdges) {
                if (k != j) {
                    int u = j.other(node);
                    int w = k.other(node);
                    double sumWeight = j.weight() + k.weight();

                    dijkstraLocalSearch.searchGraph(ranks, u, node, sumWeight);
                    if (dijkstraLocalSearch.distTo(w) > sumWeight) {
                        shortcuts.add(new Edge(u, w, sumWeight, true));
                        graph.addEdge(new Edge(u, w, sumWeight, true));
                    }
                }
            }
        }
    }
    

    public static void main(String[] args) {
        try {
            EdgeWeightedGraph graph = ParseGraph
                    .parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
            ContractionHierachiesPreprocessor ch = new ContractionHierachiesPreprocessor(graph);
            System.out.println(ch.shortcuts.size());
            StringBuilder sb = new StringBuilder();
            sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");
            for (Integer v : ch.ranks.keySet()) {
                sb.append(v).append(" ").append(ch.ranks.get(v)).append("\n");
            }
            for (Edge e : ch.graph.edges()) {
                if (e.isShortcut()) {
                    sb.append(e).append(" 1").append("\n");
                } else {
                    sb.append(e).append(" -1").append("\n");
                }
            }
            File output = new File("denmark_processed.graph");
            FileWriter fw = new FileWriter(output);
            fw.write(sb.toString());
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
