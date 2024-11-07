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
    // private DijkstraLocalSearch simulateDijkstra;
    private DijkstraLocalSearch dijkstraLocalSearch;
    private Map<Integer, Integer> ranks;
    private Set<Edge> shortcuts;
    private PriorityQueue<Integer> pq;
    private int[] deletedNeighbors;
    HashMap<Integer, Set<Integer>> visitedEdges;

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
        visitedEdges = new HashMap<>();
        // simulateDijkstra = new DijkstraLocalSearch(graph);
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
            // System.out.println("Node: " + v + " with rank: " + rank);
            ranks.put(v, rank); // ... calculate rank before adding to PQ
            pq.add(v);
            if (v % 2000 == 0) {
                long after = System.nanoTime();
                System.out.println(v + " in time in seconds: " + (after - before));
            }
        }

        // for (int i = 0; i < graph.V(); i++) {
        // int v = pq.poll();
        // System.out.println("V: " + v + " with rank: " + ranks.get(v));
        // }

        System.out.println("Done simulating");
        int scCount = 0;
        while (!pq.isEmpty()) {
            int v = pq.poll();
            int rank = ranks.get(v);
            if (pq.peek() != null && calculateRank(pq.peek()) < rank) {
                pq.add(v);
                // out.println(v + " pqSize: " + pq.size());
            } else {
                ranks.put(v, rank); // recalculate rank here??
                // test
                // if (graph.degree(v) > 5) {
                //     continue;
                // }
                contract(v);
                for (Edge edge : graph.adj(v)) {
                    deletedNeighbors[edge.other(v)]++;
                }
                scCount++;
                if (scCount % 2000 == 0) {
                    long after = System.nanoTime();
                    before = System.nanoTime();
                    System.out.println("Time: " + (after - before) + " contracted v:" + scCount);
                }
            }
        }
    }

    public void contract(int node) {
        List<Edge> adjacentEdges = new ArrayList<Edge>();
        for (Edge e : graph.adj(node)) {
            adjacentEdges.add(e);
        }
        HashMap<Integer, Set<Integer>> shortcutTracker = new HashMap<>();
        Set<Integer> ignoringEdgesBetweenTheseAndNode = new HashSet<>();

        for (Edge j : adjacentEdges) {
            for (Edge k : adjacentEdges) {
                if (!k.equals(j)) {
                    int u = j.other(node);
                    int w = k.other(node);
                    double sumWeight = j.weight() + k.weight();

                    if (!edgeExistsIn(u, w, visitedEdges)) {
                        /** 
                         * Plan: implementer dijkstralocalsearch med effektic pq reset
                        */

                        dijkstraLocalSearch.searchGraph(ranks, u, node, sumWeight, false);

                        //boolean witnessPath = hasShorterDirectEdgeToAdj(u, w, sumWeight);

                        if (dijkstraLocalSearch.distTo(w) > sumWeight  /**!witnessPath*/) {
                            if (!edgeExistsIn(u, w, shortcutTracker)) {
                                // for keeping track of added shortcuts
                                Set<Integer> setOfAssociatedShortcuts = shortcutTracker.get(u);
                                if (setOfAssociatedShortcuts == null)
                                    setOfAssociatedShortcuts = new HashSet<>();
                                setOfAssociatedShortcuts.add(w);
                                shortcutTracker.put(u, setOfAssociatedShortcuts);
                                shortcuts.add(new Edge(u, w, sumWeight, true));
                                graph.addEdge(new Edge(u, w, sumWeight, true));
                            }
                        }
                        dijkstraLocalSearch.resetDistToValues();
                    }
                    // for keeping track of ignored edges
                    ignoringEdgesBetweenTheseAndNode.add(u);
                    ignoringEdgesBetweenTheseAndNode.add(w);
                }
            }
        }
        visitedEdges.put(node, ignoringEdgesBetweenTheseAndNode);
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

                    boolean witnessPath = hasShorterDirectEdgeToAdj(u, w, sumWeight);

                    if (!witnessPath) {
                        if (!edgeExistsIn(u, w, simulatedShortcuts)) {
                            // System.out.println("Would have made shortcut: " + new Edge(u, w, sumWeight,
                            // true));
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

    private Boolean hasShorterDirectEdgeToAdj(int u, int w, double sumWeight) {
        for (Edge e : graph.adj(u)) {
            if (e.other(u) == w && e.weight() < sumWeight)
                return true;
        }
        return false;
    }

    private boolean edgeExistsIn(int u, int w, HashMap<Integer, Set<Integer>> mapOfEdges) {
        if (mapOfEdges.containsKey(u) && mapOfEdges.get(u).contains(w)) {
            return true;
        }
        if (mapOfEdges.containsKey(w) && mapOfEdges.get(w).contains(u)) {
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
