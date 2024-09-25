package org.algobench.algorithms.orthogonalvector;

public class OrthogonalVectorNaive implements OrthogonalVectorAlgorithm {

	@Override
	public int[] calculate(int[] input) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int[][] calculate(int[][] input) {
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				if (isOrthogonal(input[i], input[j])) {
					return input;
				}
			}
		}
		return null;
	}

	static boolean isOrthogonal(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == 1 && b[i] == 1) {
				return false;
			}
		}
		return true;
	}
}
