package ru.aslastin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvlMapTest {
    private AvlMap<Integer, Integer> avlMap;
    private Map<Integer, Integer> baseMap;

    private int bound = 1000;

    void stressTest(int cnt, int min_key, int max_key, long seed) {
        RandomGenerator gen = new RandomGenerator(seed);

        for (int op = 1; op <= cnt; ++op) {
            int key = gen.generateInt(min_key, max_key + 1);
            switch (gen.generateOperation()) {
                case GET -> assertEquals(baseMap.get(key), avlMap.get(key));
                case PUT -> {
                    int value = gen.generateInt();
                    assertEquals(baseMap.put(key, value), avlMap.put(key, value));
                }
                case REMOVE -> assertEquals(baseMap.remove(key), avlMap.remove(key));
            }

            int size = avlMap.size();
            assertEquals(baseMap.size(), avlMap.size());

            if (size <= bound) {
                for (var entry : baseMap.entrySet()) {
                    assertEquals(entry.getValue(), baseMap.get(entry.getKey()));
                }
                avlMap.checkProperties();
            }
        }
    }

    @BeforeEach
    void beforeEach() {
        avlMap = new AvlMap<>();
        baseMap = new HashMap<>();
    }

    @Test
    void smallStressTest() {
        stressTest(1000, 1, 100, 4085298305L);
    }

    @Test
    void bigStressTest() {
        stressTest(100_000, 1, 10_000, 88405283083L);
    }
}
