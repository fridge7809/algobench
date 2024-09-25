package org.algobench.algorithms.threesum;

import java.util.Arrays;

public class ThreeSumQuadratic implements ThreeSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 3) {
			return null;
		}
		int n = nums.length;
		int[] y = nums.clone();
		Arrays.sort(y);
		for (int i = 0; i < n; i++) {
			int target = y[i];
			int left = i + 1;
			int right = n - 1;
			while (left < right) {
				long sum = (long) y[left] + (long) y[right] + target;
				if (Thread.interrupted()) {
					return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
				}
				if (sum == 0) {
					return new int[]{target, y[left], y[right]};
				}
				if (sum < 0) {
					left++;
				} else {
					right--;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ThreeSumQuadratic";
	}
}
