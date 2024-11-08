package org.algobench.algorithms.shortestpath;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EdgeWeightedGraph {
	private static final String NEWLINE = System.getProperty("line.separator");
	private final int V;
	private int E;
	private Bag<Edge>[] adj;
	private int[] ranks;

	public EdgeWeightedGraph(int V) {
		if (V < 0) {
			throw new IllegalArgumentException("Number of vertices must be non-negative");
		} else {
			this.V = V;
			this.E = 0;
			this.adj = new Bag[V];

			for(int v = 0; v < V; ++v) {
				this.adj[v] = new Bag();
			}
		}
		ranks = new int[V];
	}

	public void removeNode(int v) {
		this.adj[v] = new Bag<>();
	}

	public EdgeWeightedGraph(int V, int E) {
		this(V);
		if (E < 0) {
			throw new IllegalArgumentException("Number of edges must be non-negative");
		} else {
			for(int i = 0; i < E; ++i) {
				int v = StdRandom.uniformInt(V);
				int w = StdRandom.uniformInt(V);
				double weight = 0.01 * (double)StdRandom.uniformInt(0, 100);
				Edge e = new Edge(v, w, weight, false);
				this.addEdge(e);
			}
		}
		ranks = new int[V];
	}

	public EdgeWeightedGraph(int V, int E, int[] ranks) {
		this(V);
		if (E < 0) {
			throw new IllegalArgumentException("Number of edges must be non-negative");
		} else {
			for(int i = 0; i < E; ++i) {
				int v = StdRandom.uniformInt(V);
				int w = StdRandom.uniformInt(V);
				double weight = 0.01 * (double)StdRandom.uniformInt(0, 100);
				Edge e = new Edge(v, w, weight, false);
				this.addEdge(e);
			}
		}
		this.ranks = ranks;
	}

	public int V() {
		return this.V;
	}

	public int E() {
		return this.E;
	}

	private void validateVertex(int v) {
		if (v < 0 || v >= this.V) {
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (this.V - 1));
		}
	}

	public void addEdge(Edge e) {
		int v = e.either();
		int w = e.other(v);
		this.validateVertex(v);
		this.validateVertex(w);
		this.adj[v].add(e);
		this.adj[w].add(e);
		++this.E;
	}

	public Iterable<Edge> adj(int v) {
		this.validateVertex(v);
		return this.adj[v];
	}

	public int degree(int v) {
		this.validateVertex(v);
		return this.adj[v].size();
	}

	public Iterable<Edge> edges() {
		Bag<Edge> list = new Bag();

		for(int v = 0; v < this.V; ++v) {
			int selfLoops = 0;
			Iterator var4 = this.adj(v).iterator();

			while(var4.hasNext()) {
				Edge e = (Edge)var4.next();
				if (e.other(v) > v) {
					list.add(e);
				} else if (e.other(v) == v) {
					if (selfLoops % 2 == 0) {
						list.add(e);
					}

					++selfLoops;
				}
			}
		}
		return list;
	}

	public Bag<Edge> getAdjacentEdges(int v) {
		return adj[v];
	}

	public int getRank(int v) {
		return ranks[v];
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(this.V + " " + this.E + NEWLINE);

		for(int v = 0; v < this.V; ++v) {
			s.append("" + v + ": ");
			Iterator var3 = this.adj[v].iterator();

			while(var3.hasNext()) {
				Edge e = (Edge)var3.next();
				s.append("" + e + "  ");
			}

			s.append(NEWLINE);
		}

		return s.toString();
	}

}
