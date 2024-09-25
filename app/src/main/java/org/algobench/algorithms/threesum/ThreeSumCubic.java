package org.algobench.algorithms.threesum;

public class ThreeSumCubic implements ThreeSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 3) {
			return null;
		}
		int n = nums.length;
		for (int i = 0; i < n; i++) {
			long a = nums[i];
			for (int j = i + 1; j < n; j++) {
				long b = nums[j];
				for (int k = j + 1; k < n; k++) {
					if (Thread.interrupted()) {
						return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
					}
					long c = nums[k];
					if (a + b + c == 0) {
						return new int[]{(int) a, (int) b, (int) c};
					}
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "ThreeSumCubic";
	}
}