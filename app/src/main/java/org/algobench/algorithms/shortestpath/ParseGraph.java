package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;
import org.algobench.algorithms.hashing.Murmurhash3;
import org.graalvm.collections.Pair;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class ParseGraph {
	// ./gradlew shadowJar
	// java -cp app/build/libs/app-all.jar
	// org.algobench.algorithms.shortestpath.BidirectionalDijkstra

	public static EdgeWeightedGraph parseInput(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1 << 16);
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		int n = Integer.parseInt(tokenizer.nextToken());
		int m = Integer.parseInt(tokenizer.nextToken());

		HashMap<Integer, Pair<Double, Double>> vertices = HashMap.newHashMap(n);
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n);

		// used for keeping trakc of hashes, to not have multiple with same hash
		Map<Long, Integer> hashes = new HashMap<>();

		for (int i = 0; i < n; i++) {
			tokenizer = new StringTokenizer(reader.readLine());

			hashes.put(Long.parseLong(tokenizer.nextToken()), n);
			
			double longitude = Double.parseDouble(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			vertices.put(n, Pair.create(longitude, latitude));
		}

		for (int i = 0; i < m; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long from = Long.parseLong(tokenizer.nextToken());
			long to = Long.parseLong(tokenizer.nextToken());
		
			int fromInt = hashes.get(from);
			int toInt = hashes.get(to);

			double weight = Double.parseDouble(tokenizer.nextToken());
			graph.addEdge(new Edge(fromInt, toInt, weight));
		}

		return graph;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraph
					.parseInput(new FileInputStream("app/src/test/resources/testing.graph"));
			
			// DijkstraShortestPath sp = new DijkstraShortestPath(graph, 0);
			BidirectionalDijkstra bsp = new BidirectionalDijkstra(graph, 0, 10);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
