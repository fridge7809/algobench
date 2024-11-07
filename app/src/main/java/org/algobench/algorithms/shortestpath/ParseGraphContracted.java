package org.algobench.algorithms.shortestpath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ParseGraphContracted {



	public static EdgeWeightedGraph parseContracted(InputStream inputStream) throws IOException {
		Scanner scanner = new Scanner(inputStream);
		int n = scanner.nextInt();
		int m = scanner.nextInt();
		int[] ranks = new int[n];
		for (int i = 0; i < n; i++) {
			int id = scanner.nextInt();
			ranks[id] = scanner.nextInt();
		}
		scanner.nextLine();
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n, m, ranks);
		for (int i = 0; i < m; i++) {
			String line = scanner.nextLine();
			String[] tokens = line.split(" ");
			String[] fromTo = tokens[0].split("-");
			int from = Integer.parseInt(fromTo[0].trim());
			int to = Integer.parseInt(fromTo[1].trim());
			Double weight = Double.parseDouble(tokens[1].replaceAll(",", ""));
			String shortcut = tokens[2];
			boolean isShortcut = shortcut.equals("-");
			Edge edge = new Edge(from, to, weight, isShortcut);
			graph.addEdge(edge);
		}

		return graph;
	}

	public static void main(String[] args) {
		try {
			EdgeWeightedGraph graph = ParseGraphContracted.parseContracted(new FileInputStream("denmark_processed.graph"));

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
