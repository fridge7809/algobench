package org.algobench.algorithms.hyperloglog;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;


class HyperLogLogTest {

	private static HyperLogLog hyperLogLog;
	private static final long[] matrix = new long[] {
			0x21ae4036L, 0x32435171L, 0xac3338cfL, 0xea97b40cL, 0x0e504b22L, 0x9ff9a4efL,
			0x111d014dL, 0x934f3787L, 0x6cd079bfL, 0x69db5c31L, 0xdf3c28edL, 0x40daf2adL,
			0x82a5891cL, 0x4659c7b0L, 0x73dc0ca8L, 0xdad3aca2L, 0x00c74c7eL, 0x9a2521e2L,
			0xf38eb6aaL, 0x64711ab6L, 0x5823150aL, 0xd13a3a9aL, 0x30a5aa04L, 0x0fb9a1daL,
			0xef785119L, 0xc9f0b067L, 0x1e7dde42L, 0xdda4a7b2L, 0x1a1c2640L, 0x297c0633L,
			0x744edb48L, 0x19adce93L
	};


	@BeforeAll
	static void setUp() {
		hyperLogLog = new HyperLogLog();
	}


	@Property
	void testHexValues() {
		Assertions.assertThat(matrix).hasSize(32);
	}

	@Provide
	Arbitrary<Integer> integerProvider() {
		return Arbitraries.integers().between(1, Integer.MAX_VALUE);
	}

	@Property
	void testLogBaseTwo(@ForAll("integerProvider") Integer n) {
		Assertions.assertThat(HyperLogLog.ln(n)).isEqualTo((int)(Math.log(n) / Math.log(2)));
	}


}