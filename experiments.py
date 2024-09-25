#!/usr/bin/env python3
import argparse
import csv
import subprocess
import time
from typing import List, Dict, Tuple

import numpy as np  # type: ignore

# Constants
FLUSH_INTERVAL = 20
TIMEOUT = 30
JAR_LOCATION: str = 'app/build/libs/app.jar'
NATIVE_BIN_LOCATION: str = 'app/build/native/nativeCompile/app'

# Use native image compiled with graal?
NATIVE = True
# How many different values of n
I_MAX: int = 30
# How many repetitions for the same n. total reps = M - WARMUP
M: int = 6
# The different values of n
NS: List[int] = [int(30 * 1.41 ** i) for i in range(I_MAX)]
# Seed for the pseudorandom number generator
SEED: int = 314159
# The PRNG object
rng = np.random.default_rng(SEED)

# The generated input:
# The dictionary maps n to a list of lists
# each list contains M lists of n ints
INPUT_DATA: Dict[int, List[List[int]]] = {
    n: [rng.integers(1, 2 ** 28, n) for _ in range(M)] for n in NS}

# Algorithm instances
INSTANCES_THREESUM: List[Tuple[str, str]] = [('threesum_cubic', JAR_LOCATION),
                                             ('threesum_quadratic',
                                              JAR_LOCATION),
                                             ('threesum_hashmap', JAR_LOCATION)]

# Algorithm instances
INSTANCES_FOURSUM: List[Tuple[str, str]] = [('foursum_cubic', JAR_LOCATION),
                                            ('foursum_quartic', JAR_LOCATION),
                                            ('foursum_hashmap', JAR_LOCATION), ]

INSTANCES_THREESUM_NATIVE: List[Tuple[str, str]] = [
    ('threesum_cubic', NATIVE_BIN_LOCATION),
    ('threesum_quadratic', NATIVE_BIN_LOCATION),
    ('threesum_hashmap', NATIVE_BIN_LOCATION)]

# Native executeable ./gradlew nativeCompile
INSTANCES_FOURSUM_NATIVE: List[Tuple[str, str]] = [
    ('foursum_cubic', NATIVE_BIN_LOCATION),
    ('foursum_quartic', NATIVE_BIN_LOCATION),
    ('foursum_hashmap', NATIVE_BIN_LOCATION)]


# run the given jar package,
# provide the given arg as the command-line 
# argument,
# feed the given input string to the stdin of the 
# process,
# and return the stdout from the process as string
def run_java(jar: str, arg: str, input: str) -> str:
    if NATIVE:
        p = subprocess.Popen([jar, arg], stdin=subprocess.PIPE,
                             stdout=subprocess.PIPE)
    else:
        p = subprocess.Popen(['java', '-jar', jar, arg], stdin=subprocess.PIPE,
                             stdout=subprocess.PIPE)
    (output, _) = p.communicate(input.encode('utf-8'), timeout=TIMEOUT)
    return output.decode('utf-8')


def measure(algorithm: str, jar: str, input: List[int]) -> float:
    input_string: str = f'{len(input)}\n' + ' '.join(map(str, input))
    start: float = time.time()
    result_string: str = run_java(jar, algorithm, input_string)
    end: float = time.time()
    assert result_string.strip() == 'null'
    return end - start


def benchmark(algorithm: str, jar: str) -> List[Tuple[int, float]]:
    results: List[Tuple[int, float]] = list()

    for n in NS:
        try:
            result_n: List[Tuple[int, float]] = list()
            for i in range(M):
                input: List[int] = INPUT_DATA[n][i]
                diff: float = measure(algorithm, jar, input)
                result_n.append((n, diff))
                print(algorithm + " " + str(i) + " " + str(n))
            results += result_n
        except subprocess.TimeoutExpired:
            break
    return results


def get_instance_by_algorithm(type: str) -> List[Tuple[str, str]]:
    if type == 'threesum':
        return INSTANCES_THREESUM_NATIVE if NATIVE else INSTANCES_THREESUM
    elif type == 'foursum':
        return INSTANCES_FOURSUM_NATIVE if NATIVE else INSTANCES_FOURSUM
    else:
        raise ValueError(f"Unkown alg type: {type}")


if __name__ == '__main__':
    # Parse args to select algorithm
    parser = argparse.ArgumentParser(description='benchmark')
    parser.add_argument('type', choices=['threesum', 'foursum'],
                        help='type of algorithm to benchmark')
    args = parser.parse_args()
    instance = get_instance_by_algorithm(args.type)

    with open('./out/results_' + args.type + '.csv', 'w') as f:
        writer = csv.DictWriter(f, fieldnames=['algorithm', 'n', 'time'])
        writer.writeheader()
        for algorithm, jar in instance:
            results: List[Tuple[int, float]] = benchmark(algorithm, jar)
            for i, (n, t) in enumerate(results):
                writer.writerow({'algorithm': algorithm, 'n': n, 'time': t})
                if (i + 1) % FLUSH_INTERVAL == 0:
                    f.flush()
