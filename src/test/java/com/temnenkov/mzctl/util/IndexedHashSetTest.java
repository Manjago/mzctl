package com.temnenkov.mzctl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexedHashSetTest {

    private IndexedHashSet<String> indexedSet;
    private Random random;

    @BeforeEach
    void setUp() {
        indexedSet = new IndexedHashSet<>();
        random = new Random(42); // фиксируем seed для повторяемости
    }

    @Test
    void testAdd() {
        assertTrue(indexedSet.add("one"));
        assertTrue(indexedSet.add("two"));
        assertTrue(indexedSet.add("three"));

        // повторное добавление не должно быть успешным
        assertFalse(indexedSet.add("one"));
        assertFalse(indexedSet.add("two"));

        assertEquals(3, indexedSet.size());
    }

    @Test
    void testRemove() {
        indexedSet.add("one");
        indexedSet.add("two");
        indexedSet.add("three");

        assertTrue(indexedSet.remove("two"));
        assertFalse(indexedSet.contains("two"));
        assertEquals(2, indexedSet.size());

        // удаление несуществующего элемента
        assertFalse(indexedSet.remove("four"));
    }

    @Test
    void testContains() {
        indexedSet.add("one");
        assertTrue(indexedSet.contains("one"));
        assertFalse(indexedSet.contains("two"));
    }

    @Test
    void testGetRandom() {
        indexedSet.add("one");
        indexedSet.add("two");
        indexedSet.add("three");

        Set<String> possibleValues = new HashSet<>();
        possibleValues.add("one");
        possibleValues.add("two");
        possibleValues.add("three");

        // проверим, что случайный элемент всегда один из добавленных
        for (int i = 0; i < 100; i++) {
            String randomElement = indexedSet.getRandom(random);
            assertTrue(possibleValues.contains(randomElement));
        }
    }

    @Test
    void testClear() {
        indexedSet.add("one");
        indexedSet.add("two");
        indexedSet.clear();
        assertEquals(0, indexedSet.size());
        assertTrue(indexedSet.isEmpty());
    }

    @Test
    void testAsList() {
        indexedSet.add("one");
        indexedSet.add("two");
        indexedSet.add("three");

        var list = indexedSet.asList();
        assertEquals(3, list.size());
        assertTrue(list.contains("one"));
        assertTrue(list.contains("two"));
        assertTrue(list.contains("three"));

        // проверим, что список немодифицируемый
        assertThrows(UnsupportedOperationException.class, () -> list.add("four"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(indexedSet.isEmpty());
        indexedSet.add("one");
        assertFalse(indexedSet.isEmpty());
    }

    @Test
    void testSize() {
        assertEquals(0, indexedSet.size());
        indexedSet.add("one");
        assertEquals(1, indexedSet.size());
        indexedSet.add("two");
        assertEquals(2, indexedSet.size());
        indexedSet.remove("one");
        assertEquals(1, indexedSet.size());
    }

    @Test
    void testAddNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> indexedSet.add(null));
    }
    @Test
    void testRemoveNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> indexedSet.remove(null));
    }

    @Test
    void testContainsNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> indexedSet.contains(null));
    }

    @Test
    void testGetRandomFromEmptyThrowsException() {
        assertThrows(IllegalStateException.class, () -> indexedSet.getRandom(random));
    }
}