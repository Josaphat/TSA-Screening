TSA-Airport-Screening
=====================

Project 2 for SWEN-342

## Compilation and Execution

### Make
If you have `make` in your path, building and running the application is simple. This assumes the required akka jars are in your `$CLASSPATH` environment variable.

```
> cd /path/to/project/
> make run
```

### Using java command-line tools
If you don't have `make`, you can build it using `javac` from a terminal. This assumes the required akka jars are in your `$CLASSPATH` environment variable.

```
> cd /path/to/project/
> javac -cp .:$CLASSPATH *.java
> java -cp .:$CLASSPATH Main
```

## Configuration
All configurable constants are `static final` variables at the top of the `Main` class in `Main.java`.

### Simulation Configuration
Various simulation constants can be modified.

- `Main.NUM_LINES` controls how many security lines are in the simulation.
- `Main.ONE_MINUTE` controls the duration of one simulated minute.
- `Main.SIMULATION_LENGTH` controls how long the Airport is open.

### Probabilities
The probabilities of certain events occurring can be modified. Probabilities are expressed in decimal form. For example, a 20% chance would be expressed as `0.20`.

- `Main.DOCUMENT_FAIL_CHANCE` the percent chance that the document check will fail.
- `Main.BAGGAGE_FAIL_CHANCE` the percent chance that the baggage scan will fail.
- `Main.BODY_FAIL_CHANCE` the percent chance that the body scan will fail.