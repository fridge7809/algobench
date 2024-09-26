package org.algobench.app;

import org.algobench.app.factory.AlgorithmContext;
import org.algobench.app.factory.AlgorithmContextFactory;
import org.algobench.app.factory.AlgorithmVariantRegister;

import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

import static org.algobench.app.InputType.*;

public class BootstrapLogic {

	private BootstrapLogic() {

	}

	static void processInput(String[] args, InputStream in) {
		Optional<AlgorithmVariantRegister> algorithmRegistry = getAlgorithmTypeFromArgs(args);
		if (algorithmRegistry.isEmpty()) {
			throw new IllegalArgumentException("Algorithm type not registered in program");
		}

		InputType inputType = determineInputDimensionType(algorithmRegistry);
		AlgorithmContext algorithmContext = AlgorithmContextFactory.getContext(algorithmRegistry.get());

		String output = null;
		if (inputType.equals(ONE_DIMENSION)) {
			int[] input = InputParser.parse(in);
			int[] result = algorithmContext.calculate(input);
			output = formatOutputArrayAsString(result);
		} else if (inputType.equals(TWO_DIMENSION)) {
			int[][] inputTwo = InputParser.parseTwoDimensional(in);
			int[][] resultTwo = algorithmContext.calculateTwoDimensional(inputTwo);
			output = formatOutputArrayAsString(resultTwo);
		} else if (inputType.equals(STREAM)) {
			Stream<Integer> input = InputParser.parseStream(in);
			output = algorithmContext.calculateStream(input);
		} else {
			throw new IllegalArgumentException("Unknown input type");
		}

		printOutput(output);
	}

	static InputType determineInputDimensionType(Optional<AlgorithmVariantRegister> register) {
		return register.map(algo -> {
					switch (algo) {
						case HYPERLOGLOG -> {
							return STREAM;
						}
						case VECTOR_NAIVE -> {
							return TWO_DIMENSION;
						}
						default -> {
							return ONE_DIMENSION;
						}

					}
				}).orElse(ONE_DIMENSION);
	}

	static String formatOutputArrayAsString(int[] output) {
		if (output == null) {
			return "null";
		}
		return String.format("%d %d %d", output[0], output[1], output[2]);
	}

	static String formatOutputArrayAsString(int[][] output) {
		if (output == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		for (int[] ints : output) {
			for (int anInt : ints) {
				sb.append(String.format("%d", anInt));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	static void printOutput(String output) {
		if (output != null) {
			System.out.println(output);
		}
	}

	static Optional<AlgorithmVariantRegister> getAlgorithmTypeFromArgs(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException("Expected exactly 1 argument but got null");
		}
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected exactly 1 argument but got " + args.length);
		}
		return AlgorithmVariantRegister.fromString(args[0]);
	}
}