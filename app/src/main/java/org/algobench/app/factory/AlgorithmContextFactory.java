package org.algobench.app.factory;

import org.algobench.algorithms.foursum.FourSumContext;
import org.algobench.algorithms.foursum.FourSumCubic;
import org.algobench.algorithms.foursum.FourSumHashmap;
import org.algobench.algorithms.foursum.FourSumQuartic;
import org.algobench.algorithms.orthogonalvector.OrthogonalVector;
import org.algobench.algorithms.orthogonalvector.OrthogonalVectorContext;
import org.algobench.algorithms.orthogonalvector.OrthogonalVectorNaive;
import org.algobench.algorithms.threesum.*;

public class AlgorithmContextFactory {
	public static AlgorithmContext getContext(AlgorithmVariantRegister registry) {
		return switch (registry) {
			case THREESUM_CUBIC -> new ThreeSumContext(new ThreeSumCubic());
			case THREESUM_QUADRATIC ->
					new ThreeSumContext(new ThreeSumQuadratic());
			case THREESUM_HASHMAP -> new ThreeSumContext(new ThreeSumHashmap());
			case THREESUM_HASHMAPNONDISTINCT ->
					new ThreeSumContext(new ThreeSumHashmapNonDistinct());
			case FOURSUM_QUARTIC -> new FourSumContext(new FourSumQuartic());
			case FOURSUM_HASHMAP -> new FourSumContext(new FourSumHashmap());
			case FOURSUM_CUBIC -> new FourSumContext(new FourSumCubic());
			case VECTOR_NAIVE -> new OrthogonalVectorContext(new OrthogonalVectorNaive());
			case null ->
					throw new IllegalArgumentException("No algorithm found");
			// Add cases for other algorithm contexts
			// case OTHER_ALGORITHM_TYPE -> new OtherAlgorithmContext(...);
		};
	}
}
