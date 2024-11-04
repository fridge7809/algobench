package org.algobench.algorithms.shortestpath;

import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class ParseGraphContracted {


	static HashMap<Integer, Integer> rank = new HashMap<>();

	public static EdgeWeightedGraph parseContracted(InputStream inputStream) throws IOException {
		Scanner scanner = new Scanner(inputStream);
		int n = scanner.nextInt();
		int m = scanner.nextInt();
		EdgeWeightedGraph graph = new EdgeWeightedGraph(n, m);
		//scanner.nextLine();
		for (int i = 0; i < n; i++) {
			int id = scanner.nextInt();
			int rank = scanner.nextInt();
		}
		for (int i = 0; i < m; i++) {
			int v = scanner.nextInt();
			String line = scanner.nextLine();
			String[] tokens = line.split(" ");
			String fromTo = tokens[0];
			Double weight = Double.parseDouble(tokens[1].replaceAll(",", ""));
			String shortcut = tokens[2];
			boolean isShortcut = shortcut.equals("-");
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
