package ru.aslastin;

import java.util.*;

@Profile(showCallTree = true)
public class AvlMap<K, V> extends AbstractMap<K, V> {
    private Node<K, V> root;
    private int size;

    private final Comparator<? super K> comparator;

    public AvlMap(Comparator<? super K> comparator) {
        this.root = null;
        this.size = 0;
        this.comparator = comparator;
    }

    public AvlMap() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    private int compare(K left, K right) {
        return comparator == null ?
                ((Comparable<? super K>) left).compareTo(right) : comparator.compare(left, right);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        var node = find(root, (K) key);
        return node == null ? null : node.value;
    }

    @Override
    public V put(K key, V value) {
        var node = find(root, key);
        if (node != null) {
            V prevValue = node.value;
            node.value = value;
            return prevValue;
        }
        root = putIfAbsent(root, key, value);
        ++size;
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        V prevValue = null;
        Node<K, V> prevNode = find(root, (K) key);
        if (prevNode != null) {
            prevValue = prevNode.value;
            root = remove(root, (K) key);
            --size;
        }
        return prevValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return find(root, (K) key) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        return containsValue(root, (V) value);
    }

    public void checkProperties() {
        checkProperties(root);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();
        entrySet(root, set);
        return set;
    }

    private Node<K, V> find(Node<K, V> node, K key) {
        while (node != null) {
            int compareResult = compare(key, node.key);
            if (compareResult == 0) {
                return node;
            }
            node = compareResult < 0 ? node.left : node.right;
        }
        return null;
    }

    private Node<K, V> putIfAbsent(Node<K, V> node, K key, V value) {
        if (node == null) {
            return new Node<>(key, value);
        }
        int compareResult = compare(key, node.key);
        if (compareResult < 0) {
            connectLeft(node, putIfAbsent(node.left, key, value));
        } else {
            connectRight(node, putIfAbsent(node.right, key, value));
        }
        return balance(node);
    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int compareResult = compare(key, node.key);
        if (compareResult == 0) {
            if (node.right == null) {
                return node.left;
            }
            Node<K, V> next = findMin(node.right);
            Node<K, V> nextRight = removeMin(node.right);
            connectLeft(next, node.left);
            connectRight(next, nextRight);
            return balance(next);
        } else if (compareResult < 0) {
            connectLeft(node, remove(node.left, key));
        } else {
            connectRight(node, remove(node.right, key));
        }
        return balance(node);
    }

    private Node<K, V> findMin(Node<K, V> node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node<K, V> removeMin(Node<K, V> node) {
        assert node != null;
        if (node.left == null) {
            return node.right;
        }
        connectLeft(node, removeMin(node.left));
        return balance(node);
    }

    private boolean containsValue(Node<K, V> node, V value) {
        if (node == null) {
            return false;
        }
        if (Objects.equals(node.value, value) || containsValue(node.left, value)) {
            return true;
        }
        return containsValue(node.right, value);
    }

    private void checkProperties(Node<K, V> node) {
        if (node == null) {
            return;
        }
        int balanceFactor = getBalanceFactor(node);
        if (balanceFactor < -1 || balanceFactor > 1) {
            String msg = String.format("%s with balanceFactor = %d, but expected between [-1, 1]", node, balanceFactor);
            throw new RuntimeException(msg);
        }
        checkProperties(node.left);
        checkProperties(node.right);
    }

    private void entrySet(Node<K, V> node, Set<Entry<K, V>> set) {
        if (node == null) {
            return;
        }
        entrySet(node.left, set);
        set.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
        entrySet(node.right, set);
    }

    private Node<K, V> balance(Node<K, V> node) {
        Node<K, V> parent = node.parent;
        updateDepth(node);
        if (getBalanceFactor(node) == -2) {
            if (getBalanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
                connectLeft(node, node.left);
            }
            Node<K, V> newNode = rotateRight(node);
            newNode.parent = parent;
            return newNode;
        }
        if (getBalanceFactor(node) == 2) {
            if (getBalanceFactor(node.right) < 0) {
                node.right = rotateRight(node.right);
                connectRight(node, node.right);
            }
            Node<K, V> newNode = rotateLeft(node);
            newNode.parent = parent;
            return newNode;
        }
        return node;
    }

    private Node<K, V> rotateLeft(Node<K, V> prev) {
        if (prev == null || prev.right == null) {
            return prev;
        }
        Node<K, V> next = prev.right;
        connectRight(prev, next.left);
        connectLeft(next, prev);
        updateDepth(prev);
        updateDepth(next);
        return next;
    }

    private Node<K, V> rotateRight(Node<K, V> prev) {
        if (prev == null || prev.left == null) {
            return prev;
        }
        Node<K, V> next = prev.left;
        connectLeft(prev, next.right);
        connectRight(next, prev);
        updateDepth(prev);
        updateDepth(next);
        return next;
    }

    private void connectLeft(Node<K, V> node, Node<K, V> child) {
        if (node != null) {
            node.left = child;
        }
        if (child != null) {
            child.parent = node;
        }
    }

    private void connectRight(Node<K, V> node, Node<K, V> child) {
        if (node != null) {
            node.right = child;
        }
        if (child != null) {
            child.parent = node;
        }
    }

    private void updateDepth(Node<K, V> node) {
        if (node == null) {
            return;
        }
        node.depth = Math.max(node.getLeftDepth(), node.getRightDepth()) + 1;
    }

    private int getBalanceFactor(Node<K, V> node) {
        if (node == null) {
            return 0;
        }
        return node.getRightDepth() - node.getLeftDepth();
    }

    private static class Node<K, V> {
        Node<K, V> left, right, parent;
        K key;
        V value;
        int depth;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.depth = 1;
        }

        int getLeftDepth() {
            return left == null ? 0 : left.depth;
        }

        int getRightDepth() {
            return right == null ? 0 : right.depth;
        }

        @Override
        public String toString() {
            return String.format("Node{key=%s, value=%s}", key, value);
        }
    }
}
