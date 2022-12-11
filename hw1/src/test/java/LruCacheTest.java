import org.junit.jupiter.api.Test;
import ru.aslastin.LruCache;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class LruCacheTest {

    static <K, V> void assertContains(LruCache<K, V> cache, K key, V value) {
        var result = cache.get(key);
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
    }

    static <K, V> void assertNotContains(LruCache<K, V> cache, K key) {
        var result = cache.get(key);
        assertTrue(result.isEmpty());
    }

    @Test
    void setAndGet() {
        var cache = new LruCache<String, String>(5);

        cache.put("x", "1");
        cache.put("y", "2");
        cache.put("z", "3");

        assertContains(cache, "x", "1");
        assertContains(cache, "z", "3");
        assertContains(cache, "y", "2");

        assertNotContains(cache, "w");

        cache.put("w", "4");

        assertContains(cache, "w", "4");
    }

    @Test
    void eviction() {
        var cache = new LruCache<String, String>(2);

        cache.put("x", "1");
        cache.put("y", "2");
        cache.put("z", "3");

        assertNotContains(cache, "x");
        assertContains(cache, "y", "2");
        assertContains(cache, "z", "3");

        cache.put("y", "4");
        cache.put("z", "5");
        cache.put("y", "6");
        cache.put("w", "7");

        assertNotContains(cache, "z");
        assertContains(cache, "y", "6");
        assertContains(cache, "w", "7");

        cache.get("y");
        cache.put("f", "8");

        assertNotContains(cache, "w");
        assertContains(cache, "y", "6");
        assertContains(cache, "f", "8");
    }

    @Test
    void stressTest() {
        Random random = new Random(34391);
        var cache = new LruCache<Integer, Integer>(128);

        for (int time = 0; time < 1000_000; ++time) {
            int key = random.nextInt(512);
            if (random.nextInt(2) % 2 == 0) {
                cache.put(key, time);
            } else {
                cache.get(key);
            }
        }
    }
}
