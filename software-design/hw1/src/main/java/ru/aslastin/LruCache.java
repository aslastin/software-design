package ru.aslastin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Least Recently Used Cache
public class LruCache<K, V> {
    private final int capacity;
    private final Map<K, KeyValueLinkedList.Node<K, V>> map;
    private final KeyValueLinkedList<K, V> list;

    public LruCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.list = new KeyValueLinkedList<>();
    }

    public Optional<V> get(K key) {
        assert (map.size() == list.size() && map.size() <= capacity);

        if (!map.containsKey(key)) {
            return Optional.empty();
        }

        var node = map.get(key);
        list.remove(node);
        list.pushBack(node);

        return Optional.of(node.getValue());
    }

    public void put(K key, V value) {
        assert (map.size() == list.size() && map.size() <= capacity);

        if (map.containsKey(key)) {
            var node = map.get(key);
            node.setValue(value);

            list.remove(node);
            list.pushBack(node);

            return;
        }

        if (map.size() == capacity) {
            var front = list.front();
            map.remove(front.getKey());
            list.remove(front);
        }

        var node = new KeyValueLinkedList.Node<>(key, value);
        map.put(key, node);
        list.pushBack(node);
    }
}
