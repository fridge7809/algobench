package org.algobench.app;

import org.algobench.algorithms.foursum.FourSumContext;
import org.algobench.algorithms.foursum.FourSumCubic;
import org.algobench.algorithms.foursum.FourSumHashmap;
import org.algobench.algorithms.foursum.FourSumQuartic;
import org.algobench.algorithms.threesum.*;
import org.algobench.app.factory.AlgorithmContextFactory;
import org.algobench.app.factory.AlgorithmVariantRegister;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Naming convention: testUnitUnderTest_shouldDoExpectedBehavior_whenCondition
 */
class BootstrapLogicTest {

	private static ByteArrayOutputStream output = new ByteArrayOutputStream();
	private static String validAlgorithmTypeToString;

	@BeforeAll
	static void setUp() {
		validAlgorithmTypeToString = AlgorithmVariantRegister.FOURSUM_CUBIC.toString();
		output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));
	}

	@AfterAll
	static void tearDown() {
		System.setOut(null);
	}

	@Test
	void testProcessInput_shouldPrintResult_whenGivenValidArguments() {
		String[] args = new String[]{validAlgorithmTypeToString};
		String input = "3\n1 2 3";
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		BootstrapLogic.processInput(args, inputStream);
		Assertions.assertEquals("null", output.toString().trim());
	}

	@Test
	void testFormatOutputArrayAsString_shouldReturnNullString_whenInputIsNull() {
		String outputNull = BootstrapLogic.formatOutputArrayAsString((int[]) null);
		assertEquals("null", outputNull);
	}

	@Test
	void testFormatOutputArrayAsString_shouldReturnString_whenGivenExpectedOutput() {
		String output123 = BootstrapLogic.formatOutputArrayAsString(new int[]{1, 2, 3});
		assertEquals("1 2 3", output123);
	}

	@Test
	void testGetCalculationContext_shouldReturnCorrectCalculationContext_whenGivenAnAlgorithmType() {
		ThreeSumContext threeSumContextCubic = (ThreeSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.THREESUM_CUBIC);
		ThreeSumContext threeSumContextQuadratic = (ThreeSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.THREESUM_QUADRATIC);
		ThreeSumContext threeSumContextHashmap = (ThreeSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.THREESUM_HASHMAP);
		ThreeSumContext threeSumContextHashmapNonDistinct = (ThreeSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.THREESUM_HASHMAPNONDISTINCT);
		FourSumContext fourSumContextCubic = (FourSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.FOURSUM_CUBIC);
		FourSumContext fourSumContextHashmap = (FourSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.FOURSUM_HASHMAP);
		FourSumContext fourSumContextQuartic = (FourSumContext) AlgorithmContextFactory.getContext(AlgorithmVariantRegister.FOURSUM_QUARTIC);
		Assertions.assertInstanceOf(ThreeSumCubic.class, threeSumContextCubic.algorithm());
		Assertions.assertInstanceOf(ThreeSumQuadratic.class, threeSumContextQuadratic.algorithm());
		Assertions.assertInstanceOf(ThreeSumHashmap.class, threeSumContextHashmap.algorithm());
		Assertions.assertInstanceOf(ThreeSumHashmapNonDistinct.class, threeSumContextHashmapNonDistinct.algorithm());
		Assertions.assertInstanceOf(FourSumCubic.class, fourSumContextCubic.algorithm());
		Assertions.assertInstanceOf(FourSumHashmap.class, fourSumContextHashmap.algorithm());
		Assertions.assertInstanceOf(FourSumQuartic.class, fourSumContextQuartic.algorithm());
	}

	@ParameterizedTest
	@NullSource
	void testGetCalculationContext_shouldThrowException_whenGivenNull(AlgorithmVariantRegister input) {
		Assertions.assertThrows(IllegalArgumentException.class, () -> AlgorithmContextFactory.getContext(input));
	}

	@Test
	void testGetAlgorithmTypeFromArgs_shouldThrowException_whenGivenUnexpectedAmountOfArguments() {
		assertThrows(IllegalArgumentException.class, () -> BootstrapLogic.getAlgorithmTypeFromArgs(new String[0]));
		assertThrows(IllegalArgumentException.class, () -> BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"cubic", "cubic"}));
	}

	@ParameterizedTest
	@ValueSource(strings = {"test"})
	void testGetAlgorithmTypeFromArgs_shouldReturnEmptyOptional_whenGivenUnrecognizedArgument(String input) {
		assertEquals(Optional.empty(), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{input}));
	}

	@Test
	void testGetAlgorithmTypeFromArgs_shouldReturnCorrectAlgorithmType_whenGivenExpectedArgs() {
		assertEquals(Optional.of(AlgorithmVariantRegister.THREESUM_CUBIC), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"threesum_cubic"}));
		assertEquals(Optional.of(AlgorithmVariantRegister.THREESUM_QUADRATIC), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"threesum_quadratic"}));
		assertEquals(Optional.of(AlgorithmVariantRegister.THREESUM_HASHMAP), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"threesum_hashmap"}));
		assertEquals(Optional.of(AlgorithmVariantRegister.THREESUM_HASHMAPNONDISTINCT), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"threesum_hashmapnondistinct"}));
		assertEquals(Optional.of(AlgorithmVariantRegister.FOURSUM_CUBIC), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"foursum_cubic"}));
		assertEquals(Optional.of(AlgorithmVariantRegister.FOURSUM_HASHMAP), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"foursum_hashmap"}));
		assertEquals(Optional.of(AlgorithmVariantRegister.FOURSUM_QUARTIC), BootstrapLogic.getAlgorithmTypeFromArgs(new String[]{"foursum_quartic"}));
	}
}