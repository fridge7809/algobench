package org.algobench.algorithms.foursum;

import java.util.HashMap;
import java.util.Map;

public class FourSumHashmap implements FourSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 4) {
			return null;
		}
		int n = nums.length;
		// <sum, indices>
		Map<Long, int[]> map = new HashMap<>();
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				map.put((long) nums[i] + (long) nums[j], new int[]{i, j});
			}
		}
		for (int i = 0; i < n; i++) {
			long a = nums[i];
			for (int j = i + 1; j < n; j++) {
				if (Thread.interrupted()) {
					return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
				}
				long b = nums[j];
				long inverseSum = -a - b;
				int[] sum = map.get(inverseSum);
				if (sum != null && j < sum[0]) {
					int c = nums[sum[0]];
					int d = nums[sum[1]];
					return new int[]{(int) a, (int) b, c, d};
				}

			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "FourSumHashmap";
	}
}
