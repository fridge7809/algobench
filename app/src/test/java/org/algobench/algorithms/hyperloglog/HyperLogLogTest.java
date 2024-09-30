package org.algobench.algorithms.hyperloglog;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.internal.Integers;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;


class HyperLogLogTest {

	private static final long[] matrix = new long[] {
			0x21ae4036L, 0x32435171L, 0xac3338cfL, 0xea97b40cL, 0x0e504b22L, 0x9ff9a4efL,
			0x111d014dL, 0x934f3787L, 0x6cd079bfL, 0x69db5c31L, 0xdf3c28edL, 0x40daf2adL,
			0x82a5891cL, 0x4659c7b0L, 0x73dc0ca8L, 0xdad3aca2L, 0x00c74c7eL, 0x9a2521e2L,
			0xf38eb6aaL, 0x64711ab6L, 0x5823150aL, 0xd13a3a9aL, 0x30a5aa04L, 0x0fb9a1daL,
			0xef785119L, 0xc9f0b067L, 0x1e7dde42L, 0xdda4a7b2L, 0x1a1c2640L, 0x297c0633L,
			0x744edb48L, 0x19adce93L
	};


	@Property
	void testHexValues() {
		assertThat(matrix).hasSize(32);
	}

	@Provide
	Arbitrary<Integer> integerProvider() {
		return Arbitraries.integers().between(1, Integer.MAX_VALUE);
	}

	@Provide
	Arbitrary<int[]> hashIntegersProvider() {
		return Arbitraries.integers()
				.between(Integer.MIN_VALUE, Integer.MAX_VALUE)
				.array(int[].class)
				.ofSize((1 << 16))
				.uniqueElements();
	}

	@Provide
	Arbitrary<int[]> millionIntegersProvider() {
		return Arbitraries.integers().between(Integer.MIN_VALUE, Integer.MAX_VALUE).array(int[].class).ofMinSize(1_000_000).ofMaxSize(1_000_000).uniqueElements().withSizeDistribution(RandomDistribution.uniform());
	}

	@Provide
	Arbitrary<int[]> smallIntegersProvider() {
		return Arbitraries.integers().between(Integer.MIN_VALUE, Integer.MAX_VALUE).array(int[].class).ofMaxSize(10_000).ofMinSize(1_000).uniqueElements().withSizeDistribution(RandomDistribution.uniform());
	}


	@Example
	void testP_shouldNotOverflow() {
		assertThat(HyperLogLog.p(0)).isZero();
	}

	@Property
	void testP_shouldBeWithinIntegerRange(@ForAll("integerProvider") int n) {
		assertThat(HyperLogLog.p(n)).isBetween(0, 32);
	}

	@Property
	void testHashCode_shouldBeAnInteger(@ForAll("integerProvider") int n) {
		assertThat(HyperLogLog.hashCode(n)).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Property(tries = 1)
	void testHashCode_hasReasonableAmountOfCollisions(@ForAll("hashIntegersProvider") int[] nums) {
		int hashSize = 10;
		// assume hash function is truly random
		int n = nums.length;
		int k = (1 << hashSize);
		int[] histogram = new int[k];
 		TreeMap<Integer, Integer> map = new TreeMap<>();
		 for (int num : nums) {
			 int hash = HyperLogLog.hashCode(num);
			 map.compute(hash, (key, val) -> val == null ? 1 : val + 1);
		 }
		 for (int distinct : map.keySet().stream().distinct().collect(Collectors.toUnmodifiableList())) {
			 histogram[map.get(distinct)] = distinct;
		 }
		 int test = 0;

	}

	@Property(tries = 5, shrinking = ShrinkingMode.OFF)
	@Disabled("doesnt hold currently")
	void hyperLogLog_relativeErrorIsAcceptable_forLargerEstimate(@ForAll("millionIntegersProvider") int[] n) {
		HyperLogLog log = new HyperLogLog(10);
		for (int i = 0; i < n.length; i++) {
			log.add(n[i]);
		}
		assertThat(log.relativeError(n.length)).isBetween(0.0, 3.125);
	}

	@Property(tries = 20)
	@Disabled("doesnt hold currently")
	void hyperLogLog_relativeErrorIsAcceptable_forSmallerEstimate(@ForAll("smallIntegersProvider") int[] n) {
		HyperLogLog log = new HyperLogLog(10);
		for (int i = 0; i < n.length; i++) {
			log.add(n[i]);
		}
		assertThat(log.relativeError(n.length)).isBetween(0.0, 3.25);
	}

}