import csv
import matplotlib.pyplot as plt

k_values = []
actual_distribution = []
expected_distribution = []

def plot(filename: str):
    with open(filename, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        next(csvreader)

        for row in csvreader:
            k = int(row[0])

            # forgot csv was comma seperated when formatting the output data oops
            actual_str = row[1] + row[2]
            expected_str = row[3] + row[4]
            actual_str = actual_str.replace(',', '.')
            expected_str = expected_str.replace(',', '.')

            actual = float(actual_str)
            expected = float(expected_str)

            actual_percent = actual / 10000000000
            expected_percent = expected / 10000000000

            k_values.append(k)
            actual_distribution.append(actual_percent)
            expected_distribution.append(expected_percent)

    plt.figure(figsize=(10, 6))

    plt.plot(k_values, expected_distribution, 'r--', marker='x', label=r'$P_k\_expected = 2^{-k}$')
    plt.plot(k_values, actual_distribution, 'b-', marker='o', label=r'$P_k\_observed = \frac{|\{ y \in \{1, \ldots, n\} \mid p(h(y)) = k \}|}{n}$')
    plt.yscale('log')
    plt.xlabel('$k$ = position of first 1-bit')
    plt.ylabel('Probability $P_k$ (log scale)')
    plt.title('Probability of the first 1-bit appearing at the $k$-th index for integers $y \in \{1, \ldots, 10^9\}$')


    plt.legend(loc='upper right', fontsize='large')

    plt.grid(True, which="both", ls="--", linewidth=0.5)
   # plt.tight_layout()
    plt.savefig('out/hash_probability.pdf')


if __name__ == '__main__':
    plot('out/hash_probability.csv')