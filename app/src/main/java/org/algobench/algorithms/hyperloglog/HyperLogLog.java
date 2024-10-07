package org.algobench.algorithms.hyperloglog;

import org.algobench.algorithms.hashing.MatrixVectorHash;
import org.algobench.algorithms.hashing.Murmurhash3;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.stream.Stream;

public class HyperLogLog implements HyperLogLogAlgorithm {

	private byte[] registers;
	private double AM;
	private int m;
	private int p;

	public HyperLogLog(int precision) {
		setup(precision);
	}

	public HyperLogLog() {
		setup(10); // default m = 2^10 = 1024
	}

	private void setup(int p) {
		this.p = p;
		this.m = (1 << p);

		this.AM = (0.7213 / (1 + 1.079 / m)) * m * m;
		this.registers = new byte[m];
	}

	// for testing and sanity checking and plotting
	protected double relativeError(double actualCardinality) {
		if (estimate() == 0.0) {
			return 0.0;
		}
		return (estimate() - actualCardinality) / actualCardinality * 100;
	}

	public double estimate() {
		double sum = 0.0;
		int zeroRegisters = 0;
		for (int register : registers) {
			sum += 1.0 / (1 << register);
			if (register == 0) {
				zeroRegisters++;
			}
		}
		double estimate = AM / sum;

		// linear counting
		if (estimate <= (5.0 / 2.0) * m && zeroRegisters != 0) {
			estimate = m * Math.log((double) m / zeroRegisters);
		}
		// large range correction
		else if (estimate > (1.0 / 30.0) * (1L << 32)) {
			System.out.println("large");
			estimate = -(1L << 32) * Math.log(1 - (estimate / (1L << 32)));
		}
		return estimate;
	}

	// hashcode to use to map 32-bit integer into m^p-bit (default m = 1024)
	private int f(int x) {
		return ((x * 0xbc164501) & 0x7fffffff) >> ((Integer.SIZE-1) - p);
	}

	static byte p(int x) {
		if (x == 0) { // overflow protection
			return 0;
		}
		return (byte) (Integer.numberOfLeadingZeros(x) + 1);
	}

	// add n to register
	public void add(int n) {
		int x = MatrixVectorHash.hash(n);
		int j = f(x);
		registers[j] = (byte) Math.max(registers[j], p(x));
	}

	@Override
	public String calculate(Stream<Integer> input) {
		input.forEach(this::add);
		double estimate = estimate();
		return String.valueOf(estimate);
	}

	public void clearRegisters() {
		for (int i = 0; i < registers.length; i++) {
			registers[i] = 0;
		}
	}

	public static void main(String[] args) {
		HyperLogLog hll = new HyperLogLog(16);
		int actualCardinality = 4_000_000;

		for (int i = actualCardinality; i < actualCardinality * 2; i++) {
			hll.add(i);
		}

		double estimatedCardinality = hll.estimate();
		System.out.println("Estimated cardinality: " + (long) estimatedCardinality);
		System.out.println("Actual cardinality: " + actualCardinality);
		System.out.printf("Relative error: %.2f%%\n", hll.relativeError(actualCardinality));
	}


}
