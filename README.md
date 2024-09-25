# Algorithm benchmarking framework

This project is a template to benchmark algorithmic performance.
The implementation is written and tested in Java.
The benchmarking framework (input generation, output formatting etc.) is written
in python with the Anaconda python distribution.

### Dependencies

- [Anaconda](https://docs.anaconda.com/) distribution of Python
- [GraalVM JDK 22](https://openjdk.org/projects/jdk/22/)

### Step 0 - Setup env

Assuming your env has the dependencies installed, do the following:

- Create new conda env: `conda create --name <env> --file requirements.txt`
- `conda activate <env>`
- Verify project builds and test is OK `./gradlew test`
- Verify correct JDK `$JAVA_HOME/bin/java -version`. JAVA_HOME must point to
  GraalVM to use native compile.

### Step 1 - Implement an algorithm

- Implement an algorithm/variant in a new package in `org/algobench/algorithms`.
- Extend input parsing, cli arguments, input source etc. for build in
  `org/algobench/app.factory`. This is only relevant if you are doing wall-clock
  execution time measurements.

Input parser currently assumes format of standard input to be:

```java
<problem size N>\n
<problem input>
```

Wherein parser parses to `int[N]`. TODO: extend parser functionality.

### Step 2 - Build app

- `./gradlew jar` compiles to a normal jar
- `./gradlew nativeCompile` uses graal native-image to compile to a native
  binary
- Manual testing with `java -jar app/build/libs/app.jar`
- or `app/build/native/nativeCompile/app` to test the native binary

### Step 3 - Define benchmarks

#### Wall clock execution time:

_Warning:_ includes JVM startup time, JVM warmup etc. Only use for comparison
and not absolute measurements.
Using the native image may help to alleviate some of this.

- Modify `experiments.py` to define appropriate experiment to generate raw data.
- `python3 experiments.py`
- Result is in `results.csv`

#### Microbenchmarking

Project uses [JMH](https://github.com/openjdk/jmh) for microbenchmarking.
See also [JMH gradle plugin](https://github.com/melix/jmh-gradle-plugin).

- Create new benchmarks in
  `algobench/app/src/jmh/java/org/algobench/benchmarking`
- Compile and execute benchmarks `./gradlew jmh`

To generate a sequence of parameters for jmh benchmarks, use
`python3 generateParameterSequence.py | pbcopy` (mac). The result is then ready
to be pasted in a `@Params`.

### Step 4 - Plotting and post processing

**Assuming wall clock execution strategy:**
- Modify `postprocess.py` to define post processing of data and plots
- `python3 postprocess.py`
- Results in `.tex` tables with formatted data and resulting `plot` as defined
  in `postprocess.py`
