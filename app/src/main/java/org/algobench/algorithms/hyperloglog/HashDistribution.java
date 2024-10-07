package org.algobench.algorithms.hyperloglog;

import org.algobench.algorithms.hashing.MatrixVectorHash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.IntStream;

public class HashDistribution {

	public static void main(String[] args) {
		int p = 10;
		int RANGE = 0;
		if (args.length > 0) {
			RANGE = Integer.parseInt(args[0]);
		}
		RANGE = RANGE == 0 ? 1_000_000 : RANGE;
		int[] nums = IntStream.range(1, RANGE).toArray();
		int bits = 1 << p;
		int[] arr = new int[bits];
		for (int i = 1; i < nums.length; i++) {
			int hash = ((MatrixVectorHash.hash(i << 16) * 0xbc164501) & 0x7fffffff) >> (Integer.SIZE - p - 1);
			arr[hash]++;
		}

		StringBuilder builder = new StringBuilder();
		builder.append("index,amount");
		builder.append(System.lineSeparator());
		for (int i = 1; i < arr.length; i++) {
			builder.append(String.format("%d,%d", i, arr[i]));
			builder.append(System.lineSeparator());
		}
		File file = new File("./out/hash_distribution.csv");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(builder.toString());
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
