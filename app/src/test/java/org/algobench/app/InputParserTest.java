package org.algobench.app;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Naming convention: testUnitUnderTest_shouldDoExpectedBehavior_whenCondition
 */
class InputParserTest {
	
	@ParameterizedTest
	@ValueSource(strings = "1\n1 2 3")
	void testParse_shouldFormatInputCorrect_whenGivenExpectedInput(String input) {
		InputStream inputStream1 = new ByteArrayInputStream(input.getBytes());
		InputStream inputStream2 = new ByteArrayInputStream(input.getBytes());
		assertDoesNotThrow(() -> InputParser.parse(inputStream1));
		int[] result = InputParser.parse(inputStream2);
		int[] expected = {1, 2, 3};
		assertArrayEquals(expected, result);
	}

	@ParameterizedTest
	@ValueSource(strings = {"Some header \n 1 2 abc 5 5"})
	void testParse_shouldThrowException_whenGivenInvalidInput(String input) {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		assertThrows(NumberFormatException.class, () -> InputParser.parse(inputStream));
	}

	@ParameterizedTest
	@ValueSource(strings = "Some header without new line ")
	void testParse_shouldThrowException_whenGivenNoNewLines(String input) {
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		assertThrows(NoSuchElementException.class, () -> InputParser.parse(inputStream));
	}
}