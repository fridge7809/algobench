package org.algobench.algorithms.threesum;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

class ThreesumTest {

	static Predicate<int[]> sumToZero = array -> Arrays.stream(array).sum() == 0;
	private static final Set<ThreeSumAlgorithm> algorithms = Set.of(
			new ThreeSumCubic(),
			new ThreeSumHashmap(),
			new ThreeSumQuadratic()
	);

	@Provide
	Arbitrary<int[]> arrayWithThreeSumProvider() {
		return Arbitraries.integers().between(-100_000, 200_000)
				.array(int[].class)
				.uniqueElements()
				.ofMinSize(3)
				.map(arr -> {
					int a = arr[0];
					int b = arr[1];
					int c = -(a + b);
					arr[2] = c;
					return arr;
				});
	}

	@Provide
	Arbitrary<int[]> arrayWithoutThreeSumProvider() {
		return Arbitraries.integers().between(0, Integer.MAX_VALUE)
				.array(int[].class)
				.ofMinSize(3)
				.uniqueElements()
				.map(arr -> {
					int a = arr[0];
					int b = arr[1];
					int c = arr[2];
					return arr;
				});
	}

	@Provide
	Arbitrary<ThreeSumAlgorithm> algorithmProvider() {
		return Arbitraries.of(algorithms);
	}

	@Property
	void algorithmWithThreeSum(@ForAll("algorithmProvider") ThreeSumAlgorithm algorithm, @ForAll("arrayWithThreeSumProvider") int[] array) {
		int[] output = algorithm.calculate(array);
		Assertions.assertThat(output).isNotNull().hasSize(3).matches(sumToZero);
	}

	@Property
	void algorithmWithoutThreeSum(@ForAll("algorithmProvider") ThreeSumAlgorithm algorithm, @ForAll("arrayWithoutThreeSumProvider") int[] array) {
		int[] output = algorithm.calculate(array);
		Assertions.assertThat(output).isNull();
	}

	@Example
	void algorithmHandlesEdgeCase(@ForAll("algorithmProvider") ThreeSumAlgorithm algorithm) {
		Assertions.assertThat(algorithm.calculate(new int[]{4, 2147483645, 2147483647})).isNull();
		Assertions.assertThat(algorithm.calculate(new int[]{0, -2147483645, 2147483647})).isNull();
		Assertions.assertThat(algorithm.calculate(new int[]{0, 0, 0})).contains(0, 0, 0).hasSize(3);
		Assertions.assertThat(algorithm.calculate(new int[]{0})).isNull();
		Assertions.assertThat(algorithm.calculate(new int[]{})).isNull();
	}
}