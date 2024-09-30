package org.algobench.benchmarking;

import org.algobench.algorithms.hyperloglog.HyperLogLog;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HyperLogLogBenchmarks {

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(value = 1, warmups = 1)
	public void benchmarkHashCode(Blackhole bh, ExecutionState state) {
		for (int i = 0; i < state.n; i++) {
			bh.consume(HyperLogLog.hashCode(state.nums[i]));
		}
	}

	@Benchmark
	public void benchmarkExponentMath(Blackhole bh, ExecutionState state) {
		bh.consume(Math.pow(2, state.n));
	}

	@State(Scope.Benchmark)
	public static class ExecutionState {
		Random random = new Random(12345);
		int[] nums;
		@Param({"10", "1000"})
		private int n;

		@Setup(Level.Trial)
		public void setup() {
			nums = new int[n];
			for (int i = 0; i < n; i++) {
				nums[i] = random.nextInt();
			}
		}
	}
}
