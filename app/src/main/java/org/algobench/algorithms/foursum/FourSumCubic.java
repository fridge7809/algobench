package org.algobench.algorithms.foursum;

import java.util.Arrays;

public class FourSumCubic implements FourSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 4) {
			return null;
		}
		int[] array = nums.clone();
		int n = nums.length;
		Arrays.sort(array);
		for (int i = 0; i < n; i++) {
			long a = array[i];
			for (int j = i + 1; j < n; j++) {
				long b = array[j];
				int left = j + 1;
				int right = n - 1;
				while (left < right) {
					if (Thread.interrupted()) {
						return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
					}
					long c = array[left];
					long d = array[right];
					long sum = a + b + c + d;
					if (sum == 0) {
						return new int[]{(int) a, (int) b, (int) c, (int) d};
					}
					if (sum < 0) {
						left++;
					} else {
						right--;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "FourSumCubic";
	}
}
