package org.algobench.algorithms.threesum;

import java.util.HashMap;

public class ThreeSumHashmap implements ThreeSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 3) {
			return null;
		}
		int n = nums.length;
		// <Value, Index> assuming distinct values
		HashMap<Long, Integer> map = new HashMap<>();
		for (int i = 0; i < n; i++) {
			map.put((long) nums[i], i);
		}
		// Iterate over pairs
		// Lookup their inverse sum in map
		for (int i = 0; i < n; i++) {
			long a = nums[i];
			for (int j = i + 1; j < n; j++) {
				if (Thread.interrupted()) {
					return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
				}
				long b = nums[j];
				long c = -(a + b);
				Integer k = map.get(c);
				// j must be strictly less than k to ensure distinctness of values
				// otherwise k may point to an index of the i'th number that may be its own inverse sum
				// a + a + b = 0
				// {2, -4} = -2 -2 = -4
				if (k != null && j < k) {
					return new int[]{(int) a, (int) b, (int) c};
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ThreeSumHashmap";
	}
}
