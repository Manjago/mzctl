package com.temnenkov.mzctl.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellTest {

    @Test
    void testIterator2d() {
        final Cell origin = Cell.of(1, 2);
        final List<Cell> cells = origin.neighborsAndSelf().toList();
        assertEquals(9, cells.size());
        assertEquals(Arrays.asList(Cell.of(0, 1), Cell.of(0, 2), Cell.of(0, 3), Cell.of(1, 1), Cell.of(1, 2),
                Cell.of(1, 3), Cell.of(2, 1), Cell.of(2, 2), Cell.of(2, 3)), cells);

    }

    @Test
    void testIterator2dNotMe() {
        final Cell origin = Cell.of(1, 2);
        final List<Cell> cells = origin.neighbors().toList();
        assertEquals(8, cells.size());
        assertEquals(Arrays.asList(Cell.of(0, 1), Cell.of(0, 2), Cell.of(0, 3), Cell.of(1, 1), Cell.of(1, 3),
                Cell.of(2, 1), Cell.of(2, 2), Cell.of(2, 3)), cells);

    }

    @Test
    void testIterator1d() {
        final Cell origin = Cell.of(5);
        final List<Cell> cells = origin.neighborsAndSelf().toList();
        assertEquals(3, cells.size());
        assertEquals(Arrays.asList(Cell.of(4), Cell.of(5), Cell.of(6)), cells);
    }

    @Test
    void testIterator3d() {
        final Cell origin = Cell.of(1, 1, 1);
        final List<Cell> cells = origin.neighborsAndSelf().toList();
        assertEquals(27, cells.size()); // 3^3 = 27 (включая саму клетку)
        assertTrue(cells.contains(Cell.of(0, 0, 0)));
        assertTrue(cells.contains(Cell.of(2, 2, 2)));
        assertTrue(cells.contains(origin));
    }

    @Test
    void testIterator2dEdgeCase() {
        final Cell origin = Cell.of(0, 0);
        final List<Cell> cells = origin.neighbors().toList();
        assertEquals(8, cells.size());
        assertTrue(cells.contains(Cell.of(-1, -1)));
        assertTrue(cells.contains(Cell.of(1, 1)));
    }

    @Test
    void testIterator2dUniqueness() {
        final Cell origin = Cell.of(1, 2);
        final List<Cell> cells = origin.neighbors().toList();
        assertEquals(8, cells.stream().distinct().count());
    }
}