package org.algobench.benchmarking;

import org.algobench.algorithms.threesum.ThreeSumCubic;
import org.algobench.algorithms.threesum.ThreeSumHashmap;
import org.algobench.algorithms.threesum.ThreeSumQuadratic;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(iterations = 20)
@Timeout(time = 1)
@Warmup(iterations = 10)
public class ThreesumBenchmarks {

	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder()
				.include(ThreesumBenchmarks.class.getSimpleName())
				.build();
		Collection<RunResult> runResults = new Runner(opt).run();
	}

	@Benchmark
	public void threesumCubic(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.threeSumCubic.calculate(state.input));
	}

	@Benchmark
	public void threesumQuadratic(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.threeSumQuadratic.calculate(state.input));
	}

	@Benchmark
	public void threesumHashmap(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.threeSumHashmap.calculate(state.input));
	}

	@State(Scope.Thread)
	public static class ExecutionState {
		static final int SEED = 314159;
		ThreeSumCubic threeSumCubic;
		ThreeSumQuadratic threeSumQuadratic;
		ThreeSumHashmap threeSumHashmap;
		int[] input;
		Random random;

		// See readme for info on how to generate sequence of params
		@Param({"1", "1", "2", "3", "4", "6", "8", "11", "16", "23", "32", "45", "64", "91", "128", "181", "256", "362", "512", "724", "1024"})
		private int n;

		@Setup(Level.Trial)
		public void setupTrial() {
			threeSumCubic = new ThreeSumCubic();
			threeSumQuadratic = new ThreeSumQuadratic();
			threeSumHashmap = new ThreeSumHashmap();
			random = new Random(SEED);
		}

		@Setup(Level.Iteration)
		public void setupIteration() {
			input = new int[n];
			for (int i = 0; i < input.length; i++) {
				input[i] = random.nextInt(1, Integer.MAX_VALUE);
			}
		}
	}
}
