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

		HashMap<Long, Pair<Double, Double>> vertices = HashMap.newHashMap(n);
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n);

		// used for keeping trakc of hashes, to not have multiple with same hash
		Map<Long, Integer> hashes = new HashMap<>();

		for (int i = 0; i < n; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long id = (Murmurhash3.hash32(BigInteger.valueOf(Long.parseLong(tokenizer.nextToken())).toByteArray()) & 0x7FFFFFFF) % n;

			// logic here that creates the hash, checks for collisions and then creates the id with the hash or a new hash

			
			double longitude = Double.parseDouble(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			vertices.put(id, Pair.create(longitude, latitude));
		}

		for (int i = 0; i < m; i++) {
			tokenizer = new StringTokenizer(reader.readLine());
			long from = Long.parseLong(tokenizer.nextToken());
			long to = Long.parseLong(tokenizer.nextToken());
			// int fromInt = Integer.parseInt(tokenizer.nextToken());
			// int toInt = Integer.parseInt(tokenizer.nextToken());

			// System.out.println(fromInt + " to " + toInt);
			// int fromIdx = (Murmurhash3.hash32(BigInteger.valueOf(from).toByteArray()) & 0x7FFFFFFF) % n;
			// int toIdx = (Murmurhash3.hash32(BigInteger.valueOf(to).toByteArray()) & 0x7FFFFFFF) % n;
			int fromIdx = createHash(from, n);
			int toIdx = createHash(to, n);

			// int hash = (int) (from ^ (from >>> 32)); // XOR the higher and lower bits
			// if (usedHashes.contains(hash)) {
			// // Resolve collision: could apply a secondary hash function
			// hash = findNextAvailableInt(hash, usedHashes); // Custom function
			// }
			// usedHashes.add(hash);

			System.out.println(fromIdx + " to " + toIdx);
			double weight = Double.parseDouble(tokenizer.nextToken());
			graph.addEdge(new Edge(fromIdx, toIdx, weight));
		}

		return graph;
	}

	private static int createHash(long num, int n){
		// check if hash exists 
		return (Murmurhash3.hash32(BigInteger.valueOf(num).toByteArray()) & 0x7FFFFFFF) % n;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraph
					.parseInput(new FileInputStream("app/src/test/resources/testing.graph"));
			
			// Hash the vertices we want to find
			int source = (Murmurhash3.hash32(BigInteger.valueOf(0).toByteArray()) & 0x7FFFFFFF) % graph.V();
			int target = (Murmurhash3.hash32(BigInteger.valueOf(10).toByteArray()) & 0x7FFFFFFF) % graph.V();
			System.out.println(source + " s - t " + target);
			// DijkstraShortestPath sp = new DijkstraShortestPath(graph, 0);
			BidirectionalDijkstra bsp = new BidirectionalDijkstra(graph, source, target);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
