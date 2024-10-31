package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.PriorityQueue;

public class ContractionHierachiesPreprocessing {

    private EdgeWeightedGraph graph;
    private List<Integer> nodeOrder;
    private Map<Integer, Integer> nodeToRank; // maps node id to node rank
    private Set<Edge> shortcuts;

    public ContractionHierachiesPreprocessing(EdgeWeightedGraph graph) {
        this.graph = graph;
        nodeOrder = new ArrayList<>();
        nodeToRank = new HashMap<>();
        shortcuts = new HashSet<>();
        preProcessGraph();
    }

    public Set<Edge> getShortcuts() {
        return shortcuts;
    }

    public Integer calculateRank(Integer v) {
        // refactor rank calculation???
        return graph.degree(v);
    }

    private void orderNodeByImportance() {
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(
                (a, b) -> Integer.compare(calculateRank(a), calculateRank(b)));
        for (int v = 0; v < graph.V(); v++) {
            pq.add(v);
        }
        while (!pq.isEmpty()) {
            int v = pq.poll();
            nodeOrder.add(v);
            nodeToRank.put(v, graph.degree(v));
        }
    }

    public void preProcessGraph() {
        orderNodeByImportance();
        for (int v = 0; v < nodeOrder.size(); v++) {
            List<Edge> adjacentVertices = new ArrayList<Edge>();
            int contractingV = nodeOrder.get(v);
            for (Edge e : graph.adj(contractingV)) {
                int vertex = e.other(contractingV);
                // if (vertex > contractingV)
                adjacentVertices.add(e);
            }
            for (Edge j : adjacentVertices) {
                for (Edge k : adjacentVertices)
                    if (k != j) {
                        int u = j.other(contractingV);
                        int w = k.other(contractingV);
                        double sumWeight = j.weight() + k.weight();

                        /**
                         * Dijkstra localsearch searches from one adjacent vertex (u) to other adjacent
                         * vertex (w), ignoring v.
                         * We create a new shortcut if and only if, the shortest path found is larger
                         * than the sum of the weights.
                         * We do not create a shortcut, if we can find a shorter way from u to w, when
                         * ignoring v.
                         */

                        DijkstraLocalSearch localSearch = new DijkstraLocalSearch(graph, u, w, contractingV, sumWeight);

                        if (localSearch.distTo(w) > sumWeight) {
                            shortcuts.add(new Edge(u, w, sumWeight));
                            graph.addEdge(new Edge(u, w, sumWeight));
                        }
                    }
            }
        }
    }

    public static void main(String[] args) {
        try {
            EdgeWeightedGraph graph = ParseGraph
                    .parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
            ContractionHierachiesPreprocessing ch = new ContractionHierachiesPreprocessing(graph);
            StringBuilder sb = new StringBuilder();
            sb.append(graph.V()).append(" ").append(graph.E()).append("\n");
            for (Integer v : ch.nodeOrder) {
                sb.append(v).append(" ").append(ch.nodeToRank.get(v)).append("\n");
            }
            for (Edge e : graph.edges()) {
                sb.append(e).append(" 1").append('\n');
            }
            for (Edge e : ch.shortcuts) {
                sb.append(e).append(" -1").append("\n");
            }
            File output = new File("denmark_processed.graph");
            FileWriter fw = new FileWriter(output);
            fw.write(sb.toString());
            fw.close();
            System.out.println(ch.getShortcuts().size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
