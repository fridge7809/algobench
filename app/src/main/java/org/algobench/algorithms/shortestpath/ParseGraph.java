package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.*;
import org.graalvm.collections.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;



public class ParseGraph {
	// ./gradlew shadowJar
	// java -cp app/build/libs/app-all.jar
	// org.algobench.algorithms.shortestpath.BidirectionalDijkstra

	private static HashMap<Long, Integer> hashes;

	public static EdgeWeightedGraph parseInput(InputStream inputStream) throws IOException {
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
			graph.addEdge(new Edge(fromInt, toInt, weight));
		}

		return graph;
	}

	public static EdgeWeightedGraph preProcessGraph(EdgeWeightedGraph graph) {
		for (int v = 0; v < graph.V(); v++) {
			List<Edge> adjacentVertices = new ArrayList<Edge>();
			for(Edge e : graph.adj(v)){
				int vertex = e.other(v);
				if(vertex > v) adjacentVertices.add(e);
			}
			for(Edge j : adjacentVertices) {
				for(Edge k : adjacentVertices)
				if(k != j) {
					int u = j.other(v);
					int w = k.other(v);
					double sumWeight = j.weight() + k.weight();

					DijkstraEarlyStopping localSearch = new DijkstraEarlyStopping(graph, u, w);

					if(localSearch.distTo(w) <= sumWeight) {
						graph.addEdge(new Edge(u, w, sumWeight));
					}
				}
			}
		}
		return null;
	}

	public static void query(EdgeWeightedGraph graph) {
		return;
	}

	public static HashMap<Long, Integer> getHashMap(){
		return hashes;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraph
					.parseInput(new FileInputStream("app/src/test/resources/denmark.graph"));

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
