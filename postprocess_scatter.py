import csv
import matplotlib.pyplot as plt
import math

data = []

with open('out/estimation_error.csv', 'r') as csvfile:
    csvreader = csv.DictReader(csvfile)
    for row in csvreader:
        row_data = {
            'p': float(row['p']),
            'trial': int(row.get('trials')),
            'n': int(row['n']),
            'estimate': float(row['estimate']),
            'error': float(row['error'])
        }
        data.append(row_data)

p = 11
data_p = [d for d in data if d['p'] == p]
n_values = [d['n'] for d in data_p]
error_values = [d['error'] for d in data_p]
m = math.pow(2, p)
m = (1.04 / math.sqrt(m)) * 100
within_error = [m >= e >= -m for e in error_values]
plt.figure(figsize=(10, 6))
plt.scatter(n_values, error_values, alpha=0.1, label='Trial within region')
plt.fill_between(n_values, -m, m, color='green', alpha=0.2, label=f'Standard error region ±{round(m, 2)}')
n_outliers = [n for i, n in enumerate(n_values) if not within_error[i]]
error_outliers = [e for i, e in enumerate(error_values) if not within_error[i]]
plt.scatter(n_outliers, error_outliers, color='red', alpha=0.1, label='Trial outside region')

plt.xscale('log')
plt.xlabel('n (log scale)', fontsize=12)
plt.ylabel('Relative error %', fontsize=12)
plt.title(f'Relative error % for $p={p}$', fontsize=14)
plt.axhline(y=m, color='green', linestyle='-', linewidth=1)
plt.axhline(y=-m, color='green', linestyle='-', linewidth=1)
plt.legend()
plt.grid(True, which='major', linestyle='--', linewidth=0.5)
plt.minorticks_off()
plt.tight_layout()

plt.savefig('out/scatter_' + str(p) + '.pdf')