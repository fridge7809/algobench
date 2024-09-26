package org.algobench.app.factory;

import java.util.Optional;

public enum AlgorithmVariantRegister {
	THREESUM_CUBIC,
	THREESUM_QUADRATIC,
	THREESUM_HASHMAP,
	THREESUM_HASHMAPNONDISTINCT,
	FOURSUM_QUARTIC,
	FOURSUM_HASHMAP,
	FOURSUM_CUBIC,
	VECTOR_NAIVE,
	HYPERLOGLOG;
	// Add other algorithm types here
	// OTHER_ALGORITHM_TYPE;

	public static Optional<AlgorithmVariantRegister> fromString(String name) {
		return switch (name) {
			case "threesum_cubic" -> Optional.of(THREESUM_CUBIC);
			case "threesum_quadratic" -> Optional.of(THREESUM_QUADRATIC);
			case "threesum_hashmap" -> Optional.of(THREESUM_HASHMAP);
			case "threesum_hashmapnondistinct" ->
					Optional.of(THREESUM_HASHMAPNONDISTINCT);
			case "foursum_quartic" -> Optional.of(FOURSUM_QUARTIC);
			case "foursum_hashmap" -> Optional.of(FOURSUM_HASHMAP);
			case "foursum_cubic" -> Optional.of(FOURSUM_CUBIC);
			case "vector_naive" -> Optional.of(VECTOR_NAIVE);
			case "hyperloglog" -> Optional.of(HYPERLOGLOG);
			// case "other" -> Optional.of(OTHER_ALGORITHM_TYPE);
			default -> Optional.empty();
		};
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
