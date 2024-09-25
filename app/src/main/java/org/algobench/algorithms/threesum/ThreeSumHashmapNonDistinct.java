package org.algobench.algorithms.threesum;

import java.util.HashMap;

public class ThreeSumHashmapNonDistinct implements ThreeSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 3) {
			return null;
		}
		int n = nums.length;
		// <Value, Index> assuming distinct values
		HashMap<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < n; i++) {
			map.put(nums[i], i);
		}
		// Iterate over pairs
		// Lookup their inverse sum in map
		for (int i = 0; i < n; i++) {
			int a = nums[i];
			for (int j = i + 1; j < n; j++) {
				if (Thread.interrupted()) {
					return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
				}
				int b = nums[j];
				int c = -a - b;
				Integer k = map.get(c);
				if (k != null) {
					return new int[]{a, b, c};
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ThreeSumHashmapNonDistinct";
	}
}
