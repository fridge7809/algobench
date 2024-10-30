package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;
import org.algobench.algorithms.hashing.Murmurhash3;
import org.graalvm.collections.Pair;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.StringTokenizer;

import java.io.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BidirectionalDijkstraTest {

    private static EdgeWeightedGraph graph;
    private static BidirectionalDijkstra bidijkst;
    private static DijkstraShortestPath dijkst;

    @BeforeEach
    private void setup() {
        try {
            graph = ParseGraph.parseInput(new FileInputStream("src/test/resources/testing.graph"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bidijkst = new BidirectionalDijkstra(graph, 0, 10);
        dijkst = new DijkstraShortestPath(graph, 0);
    }


    @Test
    public void testCorrectShortestPath(){
        assertTrue(dijkst.distTo(10) == bidijkst.getShortestPath());
    }

    // If we didn't stop early, we would relax all edges twice (one time for each dijkstra). Therefore TotalEdges*2 should be larger than the relaxed edges
    @Test
    public void testEarlyStoppingCriteria() {
        int totalEdges = graph.E();
        assertTrue(totalEdges*2 > bidijkst.getCountRelaxedEdges());
    }

    /**
     * Notes:
     * we need the logic of taking 1000 (s,t) pairs, to test both dijkstra and bidijkstra
     * x
     */

}
