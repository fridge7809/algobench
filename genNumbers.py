import random
import argparse

# Distinct number generator
def generate_distinct_numbers(n):
    return random.sample(range(n), n)

def main():
    parser = argparse.ArgumentParser(description="Generate n distinct random numbers")
    parser.add_argument("n", type=int, help="The number of distinct random numbers to generate")

    args = parser.parse_args()

    distinct_numbers = generate_distinct_numbers(args.n)
    for i, number in enumerate(distinct_numbers):
        if i < len(distinct_numbers) - 1:
            print(number)
        else:
            print(number, end='')

if __name__ == "__main__":
    main()
