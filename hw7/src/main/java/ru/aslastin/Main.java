package ru.aslastin;

public class Main {
    private static void ApplyOperations(int cnt, int min_key, int max_key, long seed) {
        RandomGenerator gen = new RandomGenerator(seed);

        AvlMap<Integer, Integer> map = new AvlMap<>();

        for (int op = 1; op <= cnt; ++op) {
            int key = gen.generateInt(min_key, max_key + 1);
            switch (gen.generateOperation()) {
                case GET -> map.get(key);
                case REMOVE -> map.remove(key);
                case PUT -> map.put(key, gen.generateInt());
            }
        }
    }

    public static void main(String[] args) {
        ApplyOperations(10, 1, 1000, 547748385L);
    }
}
