package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SliceTest {

    @Test
    void getTotalCellCount() {
        assertEquals(9, new Slice(Cell.of(0, 0), Cell.of(2, 2)).getTotalCellCount());
        assertEquals(9, new Slice(Cell.of(1, 2), Cell.of(3, 4)).getTotalCellCount());
        assertEquals(27, new Slice(Cell.of(0, 0, 0), Cell.of(2, 2, 2)).getTotalCellCount());
        assertEquals(27, new Slice(Cell.of(2, 3, 4), Cell.of(4, 5, 6)).getTotalCellCount());
    }

    @Test
    void test2DimensionsSimple() {
        final Slice slice = new Slice(Cell.of(0, 0), Cell.of(2, 2));

        final List<Cell> expected = List.of(
                Cell.of(0, 0),
                Cell.of(0, 1),
                Cell.of(0, 2),
                Cell.of(1, 0),
                Cell.of(1, 1),
                Cell.of(1, 2),
                Cell.of(2, 0),
                Cell.of(2, 1),
                Cell.of(2, 2)
        );

        assertSliceCells(slice, expected);
    }

    @Test
    void test2DimensionsComplex() {
        final Slice slice = new Slice(Cell.of(1, 2), Cell.of(3, 4));

        final List<Cell> expected = List.of(
                Cell.of(1, 2),
                Cell.of(1, 3),
                Cell.of(1, 4),
                Cell.of(2, 2),
                Cell.of(2, 3),
                Cell.of(2, 4),
                Cell.of(3, 2),
                Cell.of(3, 3),
                Cell.of(3, 4)
        );

        assertSliceCells(slice, expected);
    }

    @Test
    void test3DimensionsSimple() {
        final Slice slice = new Slice(Cell.of(0, 0, 0), Cell.of(2, 2, 2));

        final List<Cell> expected = List.of(
                Cell.of(0, 0, 0),
                Cell.of(0, 0, 1),
                Cell.of(0, 0, 2),
                Cell.of(0, 1, 0),
                Cell.of(0, 1, 1),
                Cell.of(0, 1, 2),
                Cell.of(0, 2, 0),
                Cell.of(0, 2, 1),
                Cell.of(0, 2, 2),
                Cell.of(1, 0, 0),
                Cell.of(1, 0, 1),
                Cell.of(1, 0, 2),
                Cell.of(1, 1, 0),
                Cell.of(1, 1, 1),
                Cell.of(1, 1, 2),
                Cell.of(1, 2, 0),
                Cell.of(1, 2, 1),
                Cell.of(1, 2, 2),
                Cell.of(2, 0, 0),
                Cell.of(2, 0, 1),
                Cell.of(2, 0, 2),
                Cell.of(2, 1, 0),
                Cell.of(2, 1, 1),
                Cell.of(2, 1, 2),
                Cell.of(2, 2, 0),
                Cell.of(2, 2, 1),
                Cell.of(2, 2, 2)
        );

        assertSliceCells(slice, expected);
    }

    @Test
    void test3DimensionsComplex() {
        final Slice slice = new Slice(Cell.of(1, 2, 3), Cell.of(3, 4, 5));

        final List<Cell> expected = List.of(
                Cell.of(0+1, 0+2, 0+3),
                Cell.of(0+1, 0+2, 1+3),
                Cell.of(0+1, 0+2, 2+3),
                Cell.of(0+1, 1+2, 0+3),
                Cell.of(0+1, 1+2, 1+3),
                Cell.of(0+1, 1+2, 2+3),
                Cell.of(0+1, 2+2, 0+3),
                Cell.of(0+1, 2+2, 1+3),
                Cell.of(0+1, 2+2, 2+3),
                Cell.of(1+1, 0+2, 0+3),
                Cell.of(1+1, 0+2, 1+3),
                Cell.of(1+1, 0+2, 2+3),
                Cell.of(1+1, 1+2, 0+3),
                Cell.of(1+1, 1+2, 1+3),
                Cell.of(1+1, 1+2, 2+3),
                Cell.of(1+1, 2+2, 0+3),
                Cell.of(1+1, 2+2, 1+3),
                Cell.of(1+1, 2+2, 2+3),
                Cell.of(2+1, 0+2, 0+3),
                Cell.of(2+1, 0+2, 1+3),
                Cell.of(2+1, 0+2, 2+3),
                Cell.of(2+1, 1+2, 0+3),
                Cell.of(2+1, 1+2, 1+3),
                Cell.of(2+1, 1+2, 2+3),
                Cell.of(2+1, 2+2, 0+3),
                Cell.of(2+1, 2+2, 1+3),
                Cell.of(2+1, 2+2, 2+3)
        );

        assertSliceCells(slice, expected);
    }

    private void assertSliceCells(@NotNull Slice slice, List<Cell> expected) {
        // проверка итератора
        List<Cell> fromIterator = new ArrayList<>();
        for (Cell cell : slice) {
            fromIterator.add(cell);
        }
        assertEquals(expected, fromIterator);

        // проверка Stream
        List<Cell> fromStream = slice.stream().toList();
        assertEquals(expected, fromStream);
    }

}