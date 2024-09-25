package org.algobench.app;

import org.algobench.app.factory.AlgorithmContext;
import org.algobench.app.factory.AlgorithmContextFactory;
import org.algobench.app.factory.AlgorithmVariantRegister;

import java.io.InputStream;
import java.util.Optional;

import static org.algobench.app.InputType.ONE_DIMENSION;
import static org.algobench.app.InputType.TWO_DIMENSION;

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

		// todo refac

		String output = null;
		if (inputType.equals(ONE_DIMENSION)) {
			int[] input = InputParser.parse(in);
			int[] result = algorithmContext.calculate(input);
			output = formatOutputArrayAsString(result);
		} else if (inputType.equals(TWO_DIMENSION)) {
			int[][] inputTwo = InputParser.parseTwoDimensional(in);
			int[][] resultTwo = algorithmContext.calculateTwoDimensional(inputTwo);
			output = formatOutputArrayAsString(resultTwo);
		} else {
			throw new IllegalArgumentException("Unknown input type");
		}

		printOutput(output);
	}

	static InputType determineInputDimensionType(Optional<AlgorithmVariantRegister> register) {
		return register
				.filter(register1 -> register1.equals(AlgorithmVariantRegister.VECTOR_NAIVE))
				.map(_ -> InputType.TWO_DIMENSION)
				.orElse(ONE_DIMENSION);
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