package org.algobench.algorithms.shortestpath;
import java.util.HashSet;
import java.util.Set;

public class EdgeContainer {
    private final Set<Long> edges = new HashSet<>();

    // Encode (v, w) symmetrically so (v, w) == (w, v)
    private long encodeEdge(int v, int w) {
        int min = Math.min(v, w);
        int max = Math.max(v, w);
        return ((long) min << 32) | (max & 0xFFFFFFFFL); // Store min in higher 32 bits, max in lower 32 bits
    }

    public void addEdge(int v, int w) {
        edges.add(encodeEdge(v, w));
    }

    public boolean containsEdge(int v, int w) {
        return edges.contains(encodeEdge(v, w));
    }

    public int size() {
        return edges.size();
    }

    public Set<Edge> getAllEdges(){
        Set<Edge> edges = new HashSet<>();
        for (Long encodedEdge : this.edges) {
            int min = (int) (encodedEdge >> 32);
            int max = (int) (encodedEdge & 0xFFFFFFFFL);
            edges.add(new Edge(min, max, 0.0, false, false));
        }
        return edges;
    }
}
