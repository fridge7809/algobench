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
    // private List<Integer> nodeOrder;
    private Map<Integer, Integer> nodeToRank; // maps node id to node rank
    private Set<Edge> shortcuts;
    private PriorityQueue<Integer> pq;
    private boolean[] contractedNodes;

    public ContractionHierachiesPreprocessing(EdgeWeightedGraph graph) {
        this.graph = graph;
        // nodeOrder = new ArrayList<>();
        nodeToRank = new HashMap<>();
        shortcuts = new HashSet<>();
        contractedNodes = new boolean[graph.V()];
        orderNodeByImportance();
    }

    public Set<Edge> getShortcuts() {
        return shortcuts;
    }

    public Integer calculateRank(int v) {
        // refactor rank calculation???
        return graph.degree(v);
    }

    private void orderNodeByImportance() {
        pq = new PriorityQueue<Integer>(
                (a, b) -> Integer.compare(calculateRank(a), calculateRank(b)));
        for (int v = 0; v < graph.V(); v++) {
            pq.add(v);
        }
        while (!pq.isEmpty()) {
            int v = pq.poll();
            int importance = calculateRank(v);
            if(pq.peek() != null && calculateRank(pq.peek()) < importance) {
                pq.add(v);
            } else {
                nodeToRank.put(v, graph.degree(v));
                contractNode(v);
            }
        }
    }

    // compares whether two edges have the same vertices.
    public Boolean compareTwoEdges(Edge a, Edge b) {
        int av1 = a.either();
        int av2 = a.other(av1);
        int bv1 = b.either();
        int bv2 = b.other(bv1);

        return (av1 == bv1 && av2 == bv2) || (av1 == bv2 && av2 == bv1);
    }

    public void contractNode(int node) {
        // orderNodeByImportance();
        int scCount = 0;
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

                    /**
                     * Dijkstra localsearch searches from one adjacent vertex (u) to other adjacent
                     * vertex (w), ignoring v.
                     * We create a new shortcut if and only if, the shortest path found is larger
                     * than the sum of the weights.
                     * We do not create a shortcut, if we can find a shorter way from u to w, when
                     * ignoring v.
                     */

                    DijkstraLocalSearch localSearch = new DijkstraLocalSearch(graph, u, w, node, sumWeight, contractedNodes);

                    if (localSearch.distTo(w) > sumWeight) {
                        shortcuts.add(new Edge(u, w, sumWeight, true));
                        graph.addEdge(new Edge(u, w, sumWeight, true));
                        scCount++;
                        if (scCount % 50 == 0)
                            System.out.println(scCount);
                    }
                    contractedNodes[node] = true;
                }
            }

        }
    }

    public static void main(String[] args) {
        try {
            EdgeWeightedGraph graph = ParseGraph
                    .parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
            ContractionHierachiesPreprocessing ch = new ContractionHierachiesPreprocessing(graph);
            System.out.println(ch.shortcuts.size());
             StringBuilder sb = new StringBuilder();
             sb.append(graph.V()).append(" ").append(graph.E()).append("\n");
             for (Integer v : ch.pq) {
                 sb.append(v).append(" ").append(ch.nodeToRank.get(v)).append("\n");
             }
             for (Edge e : graph.edges()) {
                 if (e.isShortcut())  {
                     sb.append(e).append(" 1").append("\n");
                 } else {
                     sb.append(e).append(" -1").append("\n");
                 }

             }
             File output = new File("denmark_processed.graph");
             FileWriter fw = new FileWriter(output);
             fw.write(sb.toString());
             fw.close();
            System.out.println(ch.getShortcuts().size());
            System.out.println(ch.getShortcuts());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
