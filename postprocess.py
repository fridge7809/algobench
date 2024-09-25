#!/usr/bin/env python3

import csv
import matplotlib.pyplot as plt  # type: ignore
import numpy as np  # type: ignore
from typing import Dict, List


def read_results(filename: str) -> Dict[str, Dict[int, List[float]]]:
    results: Dict[str, Dict[int, List[float]]] = dict()
    with open(filename, 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            algorithm: str = row['algorithm']
            n: int = int(row['n'])
            t: float = float(row['time'])
            if algorithm not in results:
                results[algorithm] = dict()
            if n not in results[algorithm]:
                results[algorithm][n] = list()
            results[algorithm][n].append(t)
    return results


def compute_mean_std(raw: Dict[int, List[float]]) -> np.ndarray:
    result = np.zeros((len(raw), 3))
    for i, n in enumerate(sorted(raw)):
        result[i, 0] = n
        mean = np.mean(raw[n])
        std = np.std(raw[n], ddof=1)

        # Normalize by n^3
        result[i, 1] = mean # / (n ** 3)
        result[i, 2] = std # / (n ** 3)
    return result



def write_latex_tabular(res: np.ndarray, filename: str):
    with open(filename, 'w') as f:
        f.write(r'\begin{tabular}{rrr}' + '\n')
        f.write(r'$n$ & Average (s) & ' + 'Standard deviation (s)')
        f.write(r'\\\hline' + '\n')
        for i in range(res.shape[0]):
            fields = [str(int(res[i, 0])), f'{res[i, 1]:.6f}',
                      f'{res[i, 2]:.6f}']
            f.write(' & '.join(fields) + r'\\' + '\n')
        f.write(r'\end{tabular}' + '\n')


def plot_algorithms(res: Dict[str, np.ndarray], filename: str):
    (fig, ax) = plt.subplots()
    algorithms = ['foursum_cubic', 'foursum_hashmap']
    for algorithm in algorithms:
        ns = res[algorithm][:, 0]
        means = res[algorithm][:, 1]
        stds = res[algorithm][:, 2]
        normalized_means = means
        normalized_stds = stds
        ax.errorbar(ns, normalized_means, normalized_stds, marker='o', capsize=3.0)


    ax.set_xlabel('Number of elements $n$')
    ax.set_ylabel('Time (s)')
    ax.set_xscale('log')
    ax.set_yscale('log')
    ax.legend(['Foursum cubic algorithm',
               'Foursum hashmap algorithm'])
    fig.savefig(filename)


if __name__ == '__main__':
    raw_results: Dict[str, Dict[int, List[float]]] = read_results(
        'out/results_foursum.csv')
    refined_results: Dict[str, np.ndarray] = dict()
    for algorithm in raw_results:
        refined_results[algorithm] = compute_mean_std(raw_results[algorithm])
    plot_algorithms(refined_results, './out/plot_foursum')
