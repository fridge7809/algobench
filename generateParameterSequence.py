N: int = 1
sequence = []
for i in range(30):
    Ni = N * (2 ** 0.5) ** i
    sequence.append(f"{Ni:.0f}")

formatted_sequence = '", "'.join(sequence)
print(formatted_sequence)