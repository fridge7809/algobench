package org.algobench.algorithms.hyperloglog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ErrorEstimation {

	public static void main(String[] args) {
		// ./gradlew jar
		// java -cp app/build/libs/app.jar org.algobench.algorithms.hyperloglog.InputGenerator N 10 17 500
		// python3 postprocess_scatter.py

		int n = 5;
		int precisionStart = 10; //inclusive
		int precisionEnd = 17; // exclusive
		int pDiff = precisionEnd - precisionStart;
		int trials = 500;
		HyperLogLog[] logs = new HyperLogLog[pDiff];
		for (int i = 0; i < pDiff; i++) {
			logs[i] = new HyperLogLog(i + precisionStart);
		}
		ArrayList<Set<Integer>> inputs = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		sb.append("p,trials,n,estimate,error");
		sb.append(System.lineSeparator());

		for (int p = 0; p < pDiff; p++) {
			for (int t = 0; t < trials; t++) {
				Random random = new Random(t);
				int precision = p + precisionStart;
				inputs.add(new HashSet<>(n));
				for (int i = 0; i < n; i++) {
					int u = (int) Math.pow(10, i + 2);
					for (int j = 0; j < u; j++) {
						int next = random.nextInt();
						inputs.get(p).add(next);
						logs[p].add(next);
					}
					double estimate = logs[p].estimate();
					double err = logs[p].relativeError(inputs.get(p).size());
					logs[p].clearRegisters();
					inputs.get(p).clear();
					sb.append(String.format(Locale.US, "%d,%d,%d,%.6f,%.6f", precision, t, u, estimate, err));
					sb.append(System.lineSeparator());
				}
			}
		}
		File file = new File("out/estimation_error.csv");
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
