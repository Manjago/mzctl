package com.temnenkov.mzctl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisjointSetTest {

    private DisjointSet<String> disjointSet;

    @BeforeEach
    void setUp() {
        disjointSet = new DisjointSet<>();
        disjointSet.makeSet("A");
        disjointSet.makeSet("B");
        disjointSet.makeSet("C");
        disjointSet.makeSet("D");
    }

    @Test
    void testMakeSetAndFind() {
        assertEquals("A", disjointSet.find("A"));
        assertEquals("B", disjointSet.find("B"));
        assertEquals("C", disjointSet.find("C"));
        assertEquals("D", disjointSet.find("D"));
    }

    @Test
    void testUnion() {
        disjointSet.union("A", "B");
        assertEquals(disjointSet.find("A"), disjointSet.find("B"));

        disjointSet.union("C", "D");
        assertEquals(disjointSet.find("C"), disjointSet.find("D"));

        // разные множества, не должны быть равны
        assertNotEquals(disjointSet.find("A"), disjointSet.find("C"));

        // объединяем два множества
        disjointSet.union("B", "C");
        assertEquals(disjointSet.find("A"), disjointSet.find("D"));
    }

    @Test
    void testUnionSameSet() {
        disjointSet.union("A", "B");
        disjointSet.union("B", "A"); // повторное объединение не должно сломать структуру
        assertEquals(disjointSet.find("A"), disjointSet.find("B"));
    }

    @Test
    void testFindThrowsIfElementNotFound() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> disjointSet.find("X"));
        assertEquals("Element not found in disjoint-set: X", exception.getMessage());
    }

    @Test
    void testUnionThrowsIfElementNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> disjointSet.union("A", "X"));
        assertTrue(exception.getMessage().contains("Element 'X' not found"));
    }

    @Test
    void testMakeSetWithNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> disjointSet.makeSet(null));
        assertTrue(exception.getMessage().contains("must not be null"));
    }

    @Test
    void testUnionWithNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> disjointSet.union(null, "A"));
        assertTrue(exception.getMessage().contains("must not be null"));
    }

    @Test
    void testFindWithNullThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> disjointSet.find(null));
        assertTrue(exception.getMessage().contains("must not be null"));
    }

    @Test
    void testUnionWhenFirstRankLessThanSecond() {
        // Создаем элементы
        disjointSet.makeSet("X");
        disjointSet.makeSet("Y");
        disjointSet.makeSet("Z");

        // Создаем ситуацию, когда множество Y-Z имеет больший ранг
        disjointSet.union("Y", "Z"); // теперь ранг множества {Y,Z} стал 1

        // Теперь объединяем одиночный узел X (ранг 0) с множеством {Y,Z} (ранг 1)
        disjointSet.union("X", "Y");

        // Теперь X должен быть присоединен к Y (или Z), проверим это:
        assertEquals(disjointSet.find("X"), disjointSet.find("Y"));

        // Дополнительно проверим, что find работает правильно
        assertEquals(disjointSet.find("X"), disjointSet.find("Z"));
    }

    @Test
    void testUnionDifferentRanksCovered() {
        // Создаем элементы
        disjointSet.makeSet("X");
        disjointSet.makeSet("Y");
        disjointSet.makeSet("Z");

        // Объединяем X и Y (ранги равны, ранг увеличится до 1)
        disjointSet.union("X", "Y");

        // Теперь объединяем Z (ранг 0) и X (ранг 1)
        disjointSet.union("Z", "X"); // тут X - вторым аргументом, значит исключение не сработает

        // Теперь создадим ситуацию, когда X (ранг 1) будет первым аргументом, а Z (ранг 0) вторым:
        disjointSet.makeSet("W"); // ещё один новый элемент с рангом 0

        // теперь явно объединяем X (ранг 1) и W (ранг 0), X первым аргументом
        disjointSet.union("X", "W"); // теперь node1.rank=1, node2.rank=0, попадем в if (node1.rank >= node2.rank)

        // Если всё корректно, исключения быть не должно
        assertEquals(disjointSet.find("W"), disjointSet.find("X"));
    }
}