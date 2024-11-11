package org.algobench.algorithms.shortestpath;//

// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.Objects;

import edu.princeton.cs.algs4.StdOut;

public class Edge implements Comparable<Edge> {
	private final int v;
	private final int w;
	private final double weight;
	private final boolean isShortcut;

	public Edge(int v, int w, double weight, boolean isShortcut) {
		this.isShortcut = isShortcut;
		if (v < 0) {
			throw new IllegalArgumentException("vertex index must be a non-negative integer");
		} else if (w < 0) {
			throw new IllegalArgumentException("vertex index must be a non-negative integer");
		} else if (Double.isNaN(weight)) {
			throw new IllegalArgumentException("Weight is NaN");
		} else {
			// Ensure symmetric pairs
			if (v > w) {
				this.v = v;
				this.w = w;
			} else {
				this.v = w;
				this.w = v;
			}
			this.weight = weight;
		}
	}

	public double weight() {
		return this.weight;
	}

	public int either() {
		return this.v;
	}

	public int other(int vertex) {
		if (vertex == this.v) {
			return this.w;
		} else if (vertex == this.w) {
			return this.v;
		} else {
			throw new IllegalArgumentException("Illegal endpoint");
		}
	}

	public int compareTo(Edge that) {
		return Double.compare(this.weight, that.weight);
	}

	public String toString() {
		return String.format("%d-%d %.5f", this.v, this.w, this.weight);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Edge edge = (Edge) o;
		return (v == edge.v && w == edge.w) || (v == edge.w && w == edge.v);
	}

	/**
	 * Instead of hashcode, we need some other way of comparing whether 2 edges are the same
	 */
	@Override
	public int hashCode() {
		int sum = v + w;
		int product = v * w;
		return 31 * sum + product;
	}

	public static void main(String[] args) {
		Edge e = new Edge(12, 34, 5.67, false);
		StdOut.println(e);
	}

	public boolean isShortcut() {
		return isShortcut;
	}
}