package ru.aslastin;

import java.util.Random;

public class RandomGenerator {
    private final Random random;

    public RandomGenerator(long seed) {
        random = new Random(seed);
    }

    public int generateInt() {
        return random.nextInt();
    }

    public int generateInt(int lowerBound, int upperBound) {
        return random.nextInt(lowerBound, upperBound);
    }

    public Operation generateOperation() {
        Operation[] operations = Operation.values();
        return operations[random.nextInt(operations.length)];
    }
}
