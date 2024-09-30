package org.algobench.algorithms.hyperloglog;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class HyperLogLog implements HyperLogLogAlgorithm {

	private static final int[] matrix = new int[]{0x21ae4036, 0x32435171, 0xac3338cf, 0xea97b40c, 0x0e504b22, 0x9ff9a4ef, 0x111d014d, 0x934f3787, 0x6cd079bf, 0x69db5c31, 0xdf3c28ed, 0x40daf2ad, 0x82a5891c, 0x4659c7b0, 0x73dc0ca8, 0xdad3aca2, 0x00c74c7e, 0x9a2521e2, 0xf38eb6aa, 0x64711ab6, 0x5823150a, 0xd13a3a9a, 0x30a5aa04, 0x0fb9a1da, 0xef785119, 0xc9f0b067, 0x1e7dde42, 0xdda4a7b2, 0x1a1c2640, 0x297c0633, 0x744edb48, 0x19adce93};
	private byte[] registers;
	private double AM;
	private int m;
	private int p;

	public HyperLogLog(int p) {
		setup(p);
	}

	public HyperLogLog() {
		setup(10);
	}

	private void setup(int p) {
		this.p = p;
		this.m = (1 << p);

		this.AM = (0.7213 / (1 + 1.079 / m)) * m * m;
		this.registers = new byte[m];
	}

	// for testing
	protected double relativeError(double actualCardinality) {
		if (estimate() == 0.0) {
			return 0.0;
		}
		return Math.abs(estimate() - actualCardinality) / actualCardinality * 100;
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
			estimate = -(1L << 32) * Math.log(1 - (estimate / (1L << 32)));
		}
		return estimate;
	}

	public static int hashCode(int x) {
		int h = 0;
		for (int j = 0; j < matrix.length; j++) {
			int t = matrix[j] & x;
			int parity = Integer.bitCount(t) % 2;
			h |= (parity << j);
		}
		return h;
	}

	// hashcode to use to map 32-bit integer into m^p-bit (default 1024)
	private int f(int x) {
		return ((x * 0xbc164501) & 0x7fffffff) >> (Integer.SIZE - p - 1);
	}

	static int p(int x) {
		if (x == 0) { // overflow protection
			return 0;
		}
		return Integer.numberOfLeadingZeros(x) + 1;
	}

	// add n to register
	public void add(int n) {
		int x = hashCode(n);
		int j = f(x);
		registers[j] = (byte) Math.max(registers[j], p(x));
	}

	@Override
	public String calculate(Stream<Integer> input) {
		input.forEach(this::add);
		double estimate = estimate();
		return String.valueOf(estimate);
	}

	public static void main(String[] args) {
		HyperLogLog hll = new HyperLogLog(10);
		int actualCardinality = 10_000;

		for (int i = actualCardinality; i < actualCardinality * 2; i++) {
			hll.add(i);
		}

		double estimatedCardinality = hll.estimate();
		System.out.println("Estimated cardinality: " + (long) estimatedCardinality);
		System.out.println("Actual cardinality: " + actualCardinality);
		System.out.printf("Relative error: %.2f%%\n", hll.relativeError(actualCardinality));
	}
}
