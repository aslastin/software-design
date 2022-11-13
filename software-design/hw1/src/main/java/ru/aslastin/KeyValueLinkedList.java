package ru.aslastin;

public class KeyValueLinkedList<K, V> {
    private final Node<K, V> dummy;
    private int size;

    public KeyValueLinkedList() {
        dummy = new Node<>(null, null);
        dummy.next = dummy;
        dummy.prev = dummy;
        size = 0;
    }

    public void pushFront(Node<K, V> node) {
        assert (size >= 0);

        dummy.prev.next = node;
        node.next = dummy;
        node.prev = dummy.prev;
        dummy.prev = node;

        ++size;
    }

    public void pushBack(Node<K, V> node) {
        assert (size >= 0);

        dummy.next.prev = node;
        node.next = dummy.next;
        node.prev = dummy;
        dummy.next = node;

        ++size;
    }

    public void remove(Node<K, V> node) {
        assert (size >= 0);

        node.next.prev = node.prev;
        node.prev.next = node.next;

        --size;
    }

    public Node<K, V> front() {
        assert (size >= 0);
        return dummy.prev == dummy ? null : dummy.prev;
    }

    public Node<K, V> back() {
        assert (size >= 0);
        return dummy.next == dummy ? null : dummy.next;
    }

    public int size() {
        assert (size >= 0);
        return size;
    }

    public static class Node<K, V> {
        private final K key;
        private V value;

        Node<K, V> next, prev;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
