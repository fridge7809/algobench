package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;
import org.algobench.algorithms.hashing.Murmurhash3;
import org.graalvm.collections.Pair;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.StringTokenizer;

public class BidirectionalDijkstra {
	// ./gradlew shadowJar
	// java -cp app/build/libs/app-all.jar org.algobench.algorithms.shortestpath.BidirectionalDijkstra

	public static EdgeWeightedGraph parseInput(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1 << 16);
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		int n = Integer.parseInt(tokenizer.nextToken());
		int m = Integer.parseInt(tokenizer.nextToken());

		HashMap<Long, Pair<Double, Double>> vertices = HashMap.newHashMap(n);
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n);

		for (int i = 0; i < n; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long id = Long.parseLong(tokenizer.nextToken());
			double longitude = Double.parseDouble(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			vertices.put(id, Pair.create(longitude, latitude));
		}

		for (int i = 0; i < m; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long from = Long.parseLong(tokenizer.nextToken());
			long to = Long.parseLong(tokenizer.nextToken());
			int fromIdx = (Murmurhash3.hash32(BigInteger.valueOf(from).toByteArray()) & 0x7FFFFFFF) % n-1;
			int toIdx = (Murmurhash3.hash32(BigInteger.valueOf(to).toByteArray()) & 0x7FFFFFFF) & n-1;
			double weight = Double.parseDouble(tokenizer.nextToken());
			graph.addEdge(new Edge(fromIdx, toIdx, weight));
		}

		return graph;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = BidirectionalDijkstra.parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));
			DijkstraShortestPath sp = new DijkstraShortestPath(graph, 0);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
