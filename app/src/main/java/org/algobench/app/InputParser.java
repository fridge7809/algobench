package org.algobench.app;

import java.io.InputStream;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class InputParser {
	InputType inputType;

	private InputParser(InputType inputType) {
		this.inputType = inputType;
	}

	static int[] parse(InputStream inputStream) {
		Pattern pattern = Pattern.compile(" ");
		try (Scanner scanner = new Scanner(inputStream)) {
			scanner.nextLine();
			return Arrays.stream(scanner.nextLine().trim().split(pattern.pattern()))
					.mapToInt(Integer::parseInt)
					.toArray();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Unexpected input format");
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Please provide integers");
		}
	}

	static int[][] parseTwoDimensional(InputStream inputStream) {
		Pattern pattern = Pattern.compile("");
		try (Scanner scanner = new Scanner(inputStream)) {
			scanner.nextLine();
			return new int[][]{
					Arrays.stream(scanner.nextLine().trim().split(pattern.pattern()))
					.mapToInt(Integer::parseInt)
					.toArray()
			};
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Unexpected input format");
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Please provide integers");
		}
	}

	static Stream<Integer> parseStream(InputStream inputStream) {
		Pattern pattern = Pattern.compile("\n");
		try (Scanner scanner = new Scanner(inputStream)) {
			return Arrays.stream(scanner.nextLine().trim().split(pattern.pattern()))
					.map(Integer::parseInt);
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException("Unexpected input format");
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Please provide integers");
		}
	}
}
