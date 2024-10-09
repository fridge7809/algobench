package org.algobench.algorithms.hyperloglog;

import org.algobench.algorithms.hashing.MatrixVectorHash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class HashProbability {

	public static void main(String[] args) {
		// ./gradlew jar
		// java -cp app/build/libs/app.jar org.algobench.algorithms.hyperloglog.HashProbability RANGE BITS
		// python3 postprocess_hashprobability.py

		if (args.length != 2) {
			System.err.println("Usage: HashProbability <RANGE> <BITS>");
		}

		int RANGE = args.length == 2 ? Integer.parseInt(args[0]) : 1_000_000;
		int bits = args.length == 2 ? Integer.parseInt(args[1]) : 32;
		int[] arr = new int[bits + 1];
		IntStream.range(0, RANGE).forEach(i -> {
			byte leading = HyperLogLog.p(MatrixVectorHash.hash(i));
			arr[leading]++;
		});

		StringBuilder builder = new StringBuilder();
		builder.append("k,actualdistribution,expecteddistribution");
		builder.append(System.lineSeparator());
		for (int i = 1; i < arr.length; i++) {
			double pred = Math.pow(2, -i);
			builder.append(String.format(Locale.US, "%d,%.10f,%.10f", i, arr[i] / (double) RANGE, pred));
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
