package org.algobench.algorithms.hyperloglog;

import org.algobench.algorithms.hashing.MatrixVectorHash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.Locale;

public class ChiSquared {

	public static void main(String[] args) {
		HyperLogLog log = new HyperLogLog(10);
		int RANGE = 0;
		if (args.length > 0) {
			RANGE = Integer.parseInt(args[0]);
		}
		RANGE = RANGE == 0 ? 1_000_000_000 : RANGE;
		int[] nums = IntStream.range(1, RANGE).toArray();
		int bits = 32;
		int[] arr = new int[bits + 1];
		for (int i = 1; i < nums.length; i++) {
			int leadingZeroes = HyperLogLog.p(MatrixVectorHash.hash(i));
			arr[leadingZeroes]++;
		}

		StringBuilder builder = new StringBuilder();
		builder.append("k,observation,expected,O-E,(O-E)^2,((O-E)^2)/E");
		builder.append(System.lineSeparator());
		double chiSquared = 0;
		for (int i = 1; i < arr.length; i++) {
			double observed = arr[i] / (double) RANGE;
			double expected = Math.pow(2, -i);
			double chiSquaredi = Math.pow(observed - expected, 2) / expected;
			builder.append(String.format(Locale.US, "%d,%.10f,%.10f,%.10f",
					i,
					observed,
					expected,
					chiSquaredi));
			builder.append(System.lineSeparator());
			chiSquared += chiSquaredi;
		}

		chiSquared *= RANGE;
		int n;
		System.out.printf("Chi squared is: %.2f", chiSquared);
		File file = new File("./out/chi_squared.csv");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(builder.toString());
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
