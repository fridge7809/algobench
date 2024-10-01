package org.algobench.algorithms.hyperloglog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class HashProbability {

	public static void main(String[] args) {
		HyperLogLog log = new HyperLogLog(10);
		int RANGE = 1_000_000_000;
		int[] nums = IntStream.range(1, RANGE).toArray();
		int bits = 32;
		int[] arr = new int[bits + 1];
		for (int i = 1; i < nums.length; i++) {
			int leadingZeroes = HyperLogLog.p(HyperLogLog.hashCode(i));
			arr[leadingZeroes]++;
		}

		StringBuilder builder = new StringBuilder();
		builder.append("k,actualdistribution,expecteddistribution");
		builder.append(System.lineSeparator());
		for (int i = 1; i < arr.length; i++) {
			double pred = Math.pow(2, -i);
			builder.append(String.format("%d,%.10f,%,.10f", i, arr[i] / (double) RANGE, pred));
			builder.append(System.lineSeparator());
		}
		File file = new File("./out/hash_probability.csv");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(builder.toString());
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
