#!/usr/bin/env python3

import sys
import random

m = 256
n = int(sys.argv[1])

print(n)
for _ in range(n):
  print(''.join(str(random.randrange(0,2)) for _ in range(m)))
