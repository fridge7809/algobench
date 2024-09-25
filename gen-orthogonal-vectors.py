#!/usr/bin/env python3

import sys
import random

m = 256
n = int(sys.argv[1])

print(n)
f = open("orthogonals.txt", "w")

f.write(sys.argv[1])
f.write("\n")

for _ in range(n):
  f.write(''.join(str(random.randrange(0,2)) for _ in range(m)))
  f.write('\n')

f.close()
