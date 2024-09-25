package org.algobench.algorithms.foursum;

public class FourSumQuartic implements FourSumAlgorithm {
	@Override
	public int[] calculate(int[] nums) {
		if (nums == null || nums.length < 4) {
			return null;
		}
		int n = nums.length;
		for (int i = 0; i < n; i++) {
			long a = nums[i];
			for (int j = i + 1; j < n; j++) {
				long b = nums[j];
				for (int k = j + 1; k < n; k++) {
					long c = nums[k];
					for (int l = k + 1; l < n; l++) {
						if (Thread.interrupted()) {
							return null; // respect jmh interrupt to return early if benchmarking thread timeout is exceeded
						}
						long d = nums[l];
						if (a + b + c + d == 0) {
							return new int[]{(int) a, (int) b, (int) c, (int) d};
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "FourSumQuartic";
	}
}