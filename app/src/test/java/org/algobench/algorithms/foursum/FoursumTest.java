package org.algobench.algorithms.foursum;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

class FoursumTest {

	static Predicate<int[]> sumToZero = array -> Arrays.stream(array).sum() == 0;
	private static final Set<FourSumAlgorithm> algorithms = Set.of(
			new FourSumCubic(),
			new FourSumHashmap(),
			new FourSumQuartic()
	);

	@Provide
	Arbitrary<int[]> arrayWithFoursumProvider() {
		return Arbitraries.integers().between(-100_000, 200_000)
				.array(int[].class)
				.uniqueElements()
				.ofMinSize(4)
				.map(arr -> {
					int a = arr[0];
					int b = arr[1];
					int c = arr[2];
					int d = -(a + b + c);
					arr[3] = d;
					return arr;
				});
	}

	@Provide
	Arbitrary<int[]> arrayWithoutFoursumProvider() {
		return Arbitraries.integers().between(0, Integer.MAX_VALUE)
				.array(int[].class)
				.uniqueElements()
				.ofMinSize(4)
				.map(arr -> {
					int a = arr[0];
					int b = arr[1];
					int c = arr[2];
					int d = arr[3];
					return arr;
				});
	}

	@Provide
	Arbitrary<FourSumAlgorithm> algorithmProvider() {
		return Arbitraries.of(algorithms);
	}

	@Property
	void algorithmWithFourSum(@ForAll("algorithmProvider") FourSumAlgorithm algorithm, @ForAll("arrayWithFoursumProvider") int[] array) {
		int[] output = algorithm.calculate(array);
		Assertions.assertThat(output).isNotNull().hasSize(4).matches(sumToZero);
	}

	@Property
	void algorithmWithoutFourSum(@ForAll("algorithmProvider") FourSumAlgorithm algorithm, @ForAll("arrayWithoutFoursumProvider") int[] array) {
		int[] output = algorithm.calculate(array);
		Assertions.assertThat(output).isNull();
	}

	@Example
	void algorithmHandlesEdgeCase(@ForAll("algorithmProvider") FourSumAlgorithm algorithm) {
		Assertions.assertThat(algorithm.calculate(new int[]{4, 2147483645, 2147483647})).isNull();
		Assertions.assertThat(algorithm.calculate(new int[]{0, -2147483645, 2147483647})).isNull();
		Assertions.assertThat(algorithm.calculate(new int[]{0, 0, 0, 0})).contains(0, 0, 0, 0).hasSize(4);
		Assertions.assertThat(algorithm.calculate(new int[]{0})).isNull();
		Assertions.assertThat(algorithm.calculate(new int[]{})).isNull();
	}
}