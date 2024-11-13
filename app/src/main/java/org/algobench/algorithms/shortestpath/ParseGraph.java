package org.algobench.algorithms.shortestpath;

import org.graalvm.collections.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;



public class ParseGraph {
	// ./gradlew shadowJar
	// java -cp app/build/libs/app-all.jar
	// org.algobench.algorithms.shortestpath.BidirectionalDijkstra

	private static HashMap<Long, Integer> hashes;

	public static EdgeWeightedGraph parseGraph(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1 << 16);
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		int n = Integer.parseInt(tokenizer.nextToken());
		int m = Integer.parseInt(tokenizer.nextToken());

		HashMap<Integer, Pair<Double, Double>> vertices = HashMap.newHashMap(n);
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n);

		// map long id to int id
		hashes = new HashMap<>();

		for (int i = 0; i < n; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			hashes.put(Long.parseLong(tokenizer.nextToken()), i);

			double longitude = Double.parseDouble(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			vertices.put(i, Pair.create(longitude, latitude));
		}

		for (int i = 0; i < m; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long from = Long.parseLong(tokenizer.nextToken());
			long to = Long.parseLong(tokenizer.nextToken());

			int fromInt = hashes.get(from);
			int toInt = hashes.get(to);

			double weight = Double.parseDouble(tokenizer.nextToken());
			graph.addEdge(new Edge(fromInt, toInt, weight, false, false));
		}
		graph.setCoords(vertices);
		return graph;
	}

	public static edu.princeton.cs.algs4.EdgeWeightedGraph parseAlgsGraph(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1 << 16);
		StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
		int n = Integer.parseInt(tokenizer.nextToken());
		int m = Integer.parseInt(tokenizer.nextToken());

		HashMap<Integer, Pair<Double, Double>> vertices = HashMap.newHashMap(n);
		edu.princeton.cs.algs4.EdgeWeightedGraph graph = new edu.princeton.cs.algs4.EdgeWeightedGraph(n);

		// map long id to int id
		hashes = new HashMap<>();

		for (int i = 0; i < n; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			hashes.put(Long.parseLong(tokenizer.nextToken()), i);

			double longitude = Double.parseDouble(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			vertices.put(i, Pair.create(longitude, latitude));
		}

		for (int i = 0; i < m; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long from = Long.parseLong(tokenizer.nextToken());
			long to = Long.parseLong(tokenizer.nextToken());

			int fromInt = hashes.get(from);
			int toInt = hashes.get(to);

			double weight = Double.parseDouble(tokenizer.nextToken());
			graph.addEdge(new edu.princeton.cs.algs4.Edge(fromInt, toInt, weight));
		}
		return graph;
	}


	public static HashMap<Long, Integer> getHashMap(){
		return hashes;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraph
					.parseGraph(new FileInputStream("app/src/test/resources/denmark.graph"));

			DijkstraEarlyStopping sp = new DijkstraEarlyStopping(graph, 0, 10);
			System.out.println(sp.distTo(10));
			// BidirectionalDijkstra bsp = new BidirectionalDijkstra(graph, 0, 10);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
