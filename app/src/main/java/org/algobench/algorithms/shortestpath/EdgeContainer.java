package org.algobench.algorithms.shortestpath;

import java.util.HashMap;
import java.util.Map;

public class EdgeContainer {
    private final Map<Integer, Map<Integer, Boolean>> pairMap = new HashMap<>();

    public void addEdge(int v, int w) {
        int min = Math.min(v, w);
        int max = Math.max(v, w);
        
        pairMap
            .computeIfAbsent(min, k -> new HashMap<>())
            .put(max, true);
    }

    public boolean containsEdge(int v, int w) {
        int min = Math.min(v, w);
        int max = Math.max(v, w);
        
        return pairMap.containsKey(min) && pairMap.get(min).containsKey(max);
    }

    public int size(){
        return pairMap.size();
    }
}