import csv
import matplotlib.pyplot as plt
from streamlit.util import index_

index = []
amount = []


def plot(filename: str):
    with open(filename, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        next(csvreader)
        for line in csvfile:

            idx, amt = line.strip().split(',')

            index.append(int(idx))

            amount.append(int(amt))

    plt.figure(figsize=(10, 6))

    plt.bar(index, amount)
    plt.plot()
    plt.xlabel('Buckets = 1024')
    plt.ylabel('Sum of collisions per bucket')
    plt.title('Uniform distribution of a hash function $h$ over $10^6$ integers << 16 Bits')
    plt.legend(loc='upper right')

    plt.grid(True, which="both", ls="--", linewidth=0.5)
   # plt.tight_layout()
    plt.savefig('out/hash_distribution.pdf')


if __name__ == '__main__':
    plot('out/hash_distribution.csv')