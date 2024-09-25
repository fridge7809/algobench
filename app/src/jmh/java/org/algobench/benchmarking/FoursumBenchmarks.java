package org.algobench.benchmarking;

import org.algobench.algorithms.foursum.FourSumCubic;
import org.algobench.algorithms.foursum.FourSumHashmap;
import org.algobench.algorithms.foursum.FourSumQuartic;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Measurement(iterations = 1)
@Timeout(time = 5)
public class FoursumBenchmarks {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(FoursumBenchmarks.class.getSimpleName()).build();
		new Runner(opt).run();
	}

	@Benchmark
	public void foursumCubic(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.fourSumCubic.calculate(state.input));
	}

	//@Benchmark
	public void foursumQuartic(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.fourSumQuartic.calculate(state.input));
	}

	@Benchmark
	public void foursumHashmap(ExecutionState state, Blackhole blackhole) {
		blackhole.consume(state.fourSumHashmap.calculate(state.input));
	}

	@State(Scope.Thread)
	public static class ExecutionState {
		static final int SEED = 314159;
		FourSumCubic fourSumCubic;
		FourSumQuartic fourSumQuartic;
		FourSumHashmap fourSumHashmap;
		int[] input;
		Random random;

		// See readme for info on how to generate sequence of params
		@Param({"30", "42", "60", "85", "120", "170", "240", "339"})
		private int n;

		@Setup(Level.Trial)
		public void setupTrial() {
			fourSumCubic = new FourSumCubic();
			fourSumQuartic = new FourSumQuartic();
			fourSumHashmap = new FourSumHashmap();
			random = new Random(SEED);
		}

		@Setup(Level.Iteration)
		public void setupIteration() {
			input = new int[n];
			for (int i = 0; i < input.length; i++) {
				input[i] = random.nextInt(Integer.MAX_VALUE - 1);
			}
		}
	}

}
