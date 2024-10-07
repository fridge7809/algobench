import csv
import math


def calculate_fractions(input_file, output_file):
    results = {}

    with open(input_file, 'r') as file:
        reader = csv.reader(file)
        next(reader)

        for row in reader:
            p = int(row[0])
            n = int(row[2])
            error = float(row[4])

            a = 1.04 / math.sqrt(2 ** p)

            one_std_lower = -a * 100
            one_std_upper = a * 100
            two_std_lower = -a * 100 * 2
            two_std_upper = a * 100 * 2

            if (p, n) not in results:
                results[(p, n)] = {'within_one_std': 0, 'within_two_std': 0, 'total': 0}

            if one_std_lower <= error <= one_std_upper:
                results[(p, n)]['within_one_std'] += 1
            if two_std_lower <= error <= two_std_upper:
                results[(p, n)]['within_two_std'] += 1
            results[(p, n)]['total'] += 1

    with open(output_file, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['p', 'n', 'withinone', 'withintwo', 'trials'])

        for (p, n), result in results.items():
            within_one = int(result['within_one_std'])
            within_two = int(result['within_two_std'])
            trials = int(result['total'])
            writer.writerow([p, n, within_one, within_two, trials])

input_file = 'out/estimation_error.csv'
output_file = 'out/fractions.csv'

if __name__ == '__main__':
    calculate_fractions(input_file, output_file)
