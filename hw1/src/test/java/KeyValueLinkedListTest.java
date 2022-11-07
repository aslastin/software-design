import org.junit.jupiter.api.Test;
import ru.aslastin.KeyValueLinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class KeyValueLinkedListTest {

    static <K, V> void assertEmpty(KeyValueLinkedList<K, V> list) {
        assertNull(list.front());
        assertNull(list.back());
        assertEquals(0, list.size());
    }

    static <K, V> void assertFrontBackSize(KeyValueLinkedList<K, V> list, V expectedFront, V expectedBack,
                                           int expectedSize) {
        assertEquals(expectedFront, list.front().getValue());
        assertEquals(expectedBack, list.back().getValue());
        assertEquals(expectedSize, list.size());
    }

    static <V> KeyValueLinkedList.Node<V, V> createNode(V value) {
        return new KeyValueLinkedList.Node<>(value, value);
    }

    @Test
    void empty() {
        assertEmpty(new KeyValueLinkedList<>());
    }

    @Test
    void test3() {
        var list = new KeyValueLinkedList<Integer, Integer>();

        var front = createNode(1);
        list.pushFront(front);
        assertFrontBackSize(list, 1, 1, 1);

        var middle = createNode(2);
        list.pushBack(middle);
        assertFrontBackSize(list, 1, 2, 2);

        var back = createNode(3);
        list.pushBack(back);
        assertFrontBackSize(list, 1, 3, 3);

        list.remove(middle);
        assertFrontBackSize(list, 1, 3, 2);

        list.remove(front);
        assertFrontBackSize(list, 3, 3, 1);

        list.remove(back);
        assertEmpty(list);
    }

    @Test
    void test5() {
        var list = new KeyValueLinkedList<String, String>();

        var e3 = createNode("3");
        list.pushBack(e3);
        assertFrontBackSize(list, "3", "3", 1);

        var e4 = createNode("4");
        list.pushBack(e4);
        assertFrontBackSize(list, "3", "4", 2);

        var e2 = createNode("2");
        list.pushFront(e2);
        assertFrontBackSize(list, "2", "4", 3);

        var e1 = createNode("1");
        list.pushFront(e1);
        assertFrontBackSize(list, "1", "4", 4);

        var e5 = createNode("5");
        list.pushBack(e5);
        assertFrontBackSize(list, "1", "5", 5);

        list.remove(e4);
        assertFrontBackSize(list, "1", "5", 4);

        list.remove(e1);
        assertFrontBackSize(list, "2", "5", 3);

        list.remove(e5);
        assertFrontBackSize(list, "2", "3", 2);

        list.remove(e2);
        assertFrontBackSize(list, "3", "3", 1);

        list.remove(e3);
        assertEmpty(list);
    }

}
