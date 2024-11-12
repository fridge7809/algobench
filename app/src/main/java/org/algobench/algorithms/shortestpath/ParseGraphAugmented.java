package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.In;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ParseGraphAugmented {

	public static EdgeWeightedGraph parseAugmentedGraph(InputStream inputStream) throws IOException {
		Scanner scanner = new Scanner(inputStream);
		int n = scanner.nextInt();
		int m = scanner.nextInt();
		scanner.nextLine();
		int[] ranks = new int[n];
		for (int i = 0; i < n; i++) {
			String[] tokens = scanner.nextLine().split(" ");
			int id = Integer.parseInt(tokens[0]);
			ranks[id] = Integer.parseInt(tokens[3]);
		}
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n, ranks);
		for (int i = 0; i < m; i++) {
			int from = (int) scanner.nextLong();
			int to = (int) scanner.nextLong();
			long weight = scanner.nextInt();
			boolean contracted = scanner.next().equals("1");
			Edge edge = new Edge(from, to, weight, false, contracted);
			graph.addEdge(edge);
		}
		return graph;
	}

	public static edu.princeton.cs.algs4.EdgeWeightedGraph parseAugmentedAlgsGraph(InputStream inputStream)
			throws IOException {
		Scanner scanner = new Scanner(inputStream);
		int n = scanner.nextInt();
		int m = scanner.nextInt();
		scanner.nextLine();
		int[] ranks = new int[n];
		for (int i = 0; i < n; i++) {
			String[] tokens = scanner.nextLine().split(" ");
			int id = Integer.parseInt(tokens[0]);
			ranks[id] = Integer.parseInt(tokens[3]);
		}
		edu.princeton.cs.algs4.EdgeWeightedGraph graph = new edu.princeton.cs.algs4.EdgeWeightedGraph(n);
		for (int i = 0; i < m; i++) {
			String line = scanner.nextLine();
			String[] tokens = line.split(" ");
			String[] fromTo = tokens[0].split("-");
			int from = Integer.parseInt(fromTo[0].trim());
			int to = Integer.parseInt(fromTo[1].trim());
			Double weight = Double.parseDouble(tokens[1].replaceAll(",", ""));
			edu.princeton.cs.algs4.Edge edge = new edu.princeton.cs.algs4.Edge(from, to, weight);
			graph.addEdge(edge);
		}
		return graph;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraphAugmented
					.parseAugmentedGraph(new FileInputStream("denmark_processed.graph"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}