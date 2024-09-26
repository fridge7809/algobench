package org.algobench.algorithms.hyperloglog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

public class HyperLogLog implements HyperLogLogAlgorithm {

	// Constants
	private static final int m = 1024;
	private static final int k = 32;
	private static final double AM = 0.7213 / (1 + 1.079 / m);
	private static final int[] matrix = new int[]{0x21ae4036, 0x32435171, 0xac3338cf, 0xea97b40c, 0x0e504b22, 0x9ff9a4ef, 0x111d014d, 0x934f3787, 0x6cd079bf, 0x69db5c31, 0xdf3c28ed, 0x40daf2ad, 0x82a5891c, 0x4659c7b0, 0x73dc0ca8, 0xdad3aca2, 0x00c74c7e, 0x9a2521e2, 0xf38eb6aa, 0x64711ab6, 0x5823150a, 0xd13a3a9a, 0x30a5aa04, 0x0fb9a1da, 0xef785119, 0xc9f0b067, 0x1e7dde42, 0xdda4a7b2, 0x1a1c2640, 0x297c0633, 0x744edb48, 0x19adce93};

	private static double applyConstant(double sum) {
		return (AM * (m * m)) * (1.0 / sum);
	}

	private static Pair<Double, Integer> getRawEstimate(int[] buckets) {
		double sum = 0.0D;
		int sumEmptyBuckets = 0;
		for (int j = 1; j < buckets.length; j++) {
			if (buckets[j] == 0) {
				sumEmptyBuckets++;
			}
			sum += 1.0 / Math.pow(2, buckets[j]);
		}
		return new Pair<>(applyConstant(sum), sumEmptyBuckets);
	}

	private static double applyLargeRangeCorrection(double estimate) {
		estimate = -(Math.pow(2, 32)) * (Math.log(1 - estimate / Math.pow(2, 32)) / Math.log(2));
		return estimate;
	}

	private static boolean shouldApplyLargeRangeCorrection(double estimate) {
		return estimate > (((double) 1 / 30) * Math.pow(2, 32));
	}

	private static double applyLinearCounting(double v) {
		if (v == 0) {
			return 0;
		}
		return m * ln(m / (int) v);
	}

	private static boolean shouldApplyLinearCounting(double estimate, double v) {
		return estimate <= ((5.0 / 2.0) * m) && v > 0;
	}

	public static int ln(int bits) {
		return 31 - Integer.numberOfLeadingZeros(bits);
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

	public static int f(int x) {
		return ((x * 0xbc164501) & 0x7fffffff) >> 21;
	}

	public static int p(int x) {
		return Integer.numberOfLeadingZeros(x) + 1;
	}

	public static void main(String[] args) {
		// Initialize the HyperLogLog instance
		HyperLogLog hyperLogLog = new HyperLogLog();

		// Read input from the console
		File file = new File("out.txt");
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		Scanner finalScanner = scanner;
		Stream<Integer> integerStream = Stream.generate(() -> {
			if (finalScanner.hasNextLine()) {
				return finalScanner.nextLine();
			} else {
				return null;
			}
		}).takeWhile(Objects::nonNull).map(String::trim).filter(line -> !line.isEmpty()).map(Integer::parseInt);

		// Calculate the estimate
		String result = hyperLogLog.calculate(integerStream);

		// Output the result
		System.out.println("Estimated cardinality: " + result);

		scanner.close();
	}

	@Override
	public String calculate(Stream<Integer> input) {
		// Split universe into buckets
		int[] buckets = new int[m];

		input.forEach(i -> {
			int j = f(i);
			int x = hashCode(i);
			buckets[j] = Math.max(buckets[j], p(x));
		});


		Pair<Double, Integer> rawEstimatePair = getRawEstimate(buckets);
		double rawEstimate = rawEstimatePair.getLeft();
		double sumEmptyBuckets = rawEstimatePair.getRight();

		if (shouldApplyLinearCounting(rawEstimate, sumEmptyBuckets)) {
			System.out.println("linear counting");
			return String.valueOf(applyLinearCounting(sumEmptyBuckets));
		}

		if (shouldApplyLargeRangeCorrection(rawEstimate)) {
			System.out.println("large range");
			return String.valueOf(applyLargeRangeCorrection(rawEstimate));
		}

		return "";
	}

	private static class Pair<T, Y> {
		private final T left;
		private final Y right;

		public Pair(T first, Y second) {
			this.left = first;
			this.right = second;
		}

		public T getLeft() {
			return left;
		}

		public Y getRight() {
			return right;
		}
	}

}
