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
    private DijkstraLocalSearch simulateDijkstra;
    private DijkstraLocalSearch dijkstraLocalSearch;
    private Map<Integer, Integer> ranks;
    private Set<Edge> shortcuts;
    private PriorityQueue<Integer> pq;
    private int[] deletedNeighbors;

    /**
     * Node to self:
     * Kind of works.
     * Node order (ranks) should calculate correct amount of shortcuts that it would
     * create. This doesnt work
     * Should maybe be reset? or use different dijsktra to compute the first??
     * SimulateContract should only return an int representing the amount of
     * shortcuts that would be created
     */

    public ContractionHierachiesPreprocessor(EdgeWeightedGraph graph) {
        this.graph = graph;
        ranks = new HashMap<>();
        shortcuts = new HashSet<>();
        deletedNeighbors = new int[graph.V()];
        simulateDijkstra = new DijkstraLocalSearch(graph);
        dijkstraLocalSearch = new DijkstraLocalSearch(graph);
        orderNodeByImportance();
    }

    public Set<Edge> getShortcuts() {
        return shortcuts;
    }

    public int calculateRank(int node) {
        int amountOfShortcuts = simulateContract(node);
        int edgeDifference = edgeDifference(node, amountOfShortcuts);
        return edgeDifference + deletedNeighbors[node];
    }

    public int edgeDifference(int node, int shortcuts) {
        return shortcuts - graph.degree(node);
    }

    private void orderNodeByImportance() {
        long before = System.nanoTime();

        // prioritize nodes in the queue by their ranks...
        pq = new PriorityQueue<>((a, b) -> Integer.compare(ranks.get(a), ranks.get(b)));
        for (int v = 0; v < graph.V(); v++) {
            int rank = calculateRank(v);
            //System.out.println("Node: " + v + " with rank: " + rank);
            ranks.put(v, rank); // ... calculate rank before adding to PQ
            pq.add(v);
            if (v % 2000 == 0) {
                long after = System.nanoTime();
                System.out.println(v + " in time in seconds: " + (after - before));
            }
        }

        System.out.println("Done simulating");
        int scCount = 0;
        // outcommenting the actual ranking/contracting for now
        // while (!pq.isEmpty()) {
        // int v = pq.poll();
        // int rank = ranks.get(v);
        // if (pq.peek() != null && ranks.get(pq.peek()) < rank) {
        // pq.add(v);
        // } else {
        // ranks.put(v, rank); // recalculate rank here??
        // contract(v);
        // for (Edge edge : graph.adj(v)) {
        // deletedNeighbors[edge.other(v)]++;
        // }
        // scCount++;
        // if (scCount % 2000 == 0) {
        // long after = System.nanoTime();
        // System.out.println(scCount + " in time in seconds: " + (after - before) /
        // 1_000_000_000);
        // before = System.nanoTime();
        // }
        // }
        // }
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

                    dijkstraLocalSearch.searchGraph(ranks, u, node, sumWeight, false);
                    if (dijkstraLocalSearch.distTo(w) > sumWeight) {
                        // System.out.println(new Edge(u, w, sumWeight, true));
                        shortcuts.add(new Edge(u, w, sumWeight, true));
                        graph.addEdge(new Edge(u, w, sumWeight, true));
                    }
                }
            }
        }
    }

    public int simulateContract(int node) {
        // System.out.println("Simulating contract of V: " + node);
        List<Edge> adjacentEdges = new ArrayList<Edge>();
        for (Edge e : graph.adj(node)) {
            adjacentEdges.add(e);
        }
        HashMap<Integer, Set<Integer>> simulatedShortcuts = new HashMap<>();
        int countContractions = 0;
        for (Edge j : adjacentEdges) {
            for (Edge k : adjacentEdges) {
                if (!k.equals(j)) {
                    int u = j.other(node);
                    int w = k.other(node);
                    double sumWeight = j.weight() + k.weight();

                    // simulateDijkstra.searchGraph(ranks, u, node, sumWeight, true);

                    boolean witnessPath = hasDirectEdgeToAdj(u, w, sumWeight);

                    if (!witnessPath) {
                        if (!shortcutExists(u, w, simulatedShortcuts)) {
                            // System.out.println("Would have made shortcut: " + new Edge(u, w, sumWeight,
                            //         true));
                            Set<Integer> setOfAssociatedShortcuts = simulatedShortcuts.get(u);
                            if (setOfAssociatedShortcuts == null)
                                setOfAssociatedShortcuts = new HashSet<>();
                            setOfAssociatedShortcuts.add(w);
                            simulatedShortcuts.put(u, setOfAssociatedShortcuts);
                            countContractions++;
                        }
                    }

                    // simulateDijkstra.resetDistToValues();
                }
            }
        }
        // System.out.println("Simulating contract of V: " + node + " finished with " +
        // countContractions + " shortcuts");
        return countContractions;
    }

    private Boolean hasDirectEdgeToAdj(int u, int w, double sumWeight) {
        for (Edge e : graph.adj(u)) {
            if (e.other(u) == w && e.weight() < sumWeight)
                return true;
        }
        return false;
    }

    private boolean shortcutExists(int u, int w, HashMap<Integer, Set<Integer>> simulatedShortcuts) {
        if (simulatedShortcuts.containsKey(u) && simulatedShortcuts.get(u).contains(w)) {
            return true;
        }
        if (simulatedShortcuts.containsKey(w) && simulatedShortcuts.get(w).contains(u)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            EdgeWeightedGraph graph = ParseGraph
                    .parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
            ContractionHierachiesPreprocessor ch = new ContractionHierachiesPreprocessor(graph);
            System.out.println(ch.shortcuts.size());
            // StringBuilder sb = new StringBuilder();
            // sb.append(ch.graph.V()).append(" ").append(ch.graph.E()).append("\n");
            // for (Integer v : ch.ranks.keySet()) {
            // sb.append(v).append(" ").append(ch.ranks.get(v)).append("\n");
            // }
            // for (Edge e : ch.graph.edges()) {
            // if (e.isShortcut()) {
            // sb.append(e).append(" 1").append("\n");
            // } else {
            // sb.append(e).append(" -1").append("\n");
            // }
            // }
            // File output = new File("denmark_processed.graph");
            // FileWriter fw = new FileWriter(output);
            // fw.write(sb.toString());
            // fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
