package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Slice implements Iterable<Cell> {
    public final @NotNull Cell first;
    public final @NotNull Cell last;

    private final int totalCellCount;

    public Slice(@NotNull Cell first, @NotNull Cell last) {
        if (first.size() != last.size()) {
            throw new IllegalArgumentException("First and last cells must be the same");
        }
        if (!notGreaterThen(first, last)) {
            throw new IllegalArgumentException("Last cells must be not greater than the last");
        }
        this.first = first;
        this.last = last;
        this.totalCellCount = calculateTotalCellCount();
    }

    private boolean notGreaterThen(@NotNull Cell first, @NotNull Cell last) {
        for(int i=0; i < first.size(); ++i) {
            final int firstCoord = first.coord(i);
            final int lastCoord = last.coord(i);
            if (firstCoord > lastCoord) {
                return false;
            }
        }
        return true;
    }

    public int getTotalCellCount() {
        return totalCellCount;
    }

    public Stream<Cell> stream() {
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iterator(),
                        getTotalCellCount(),
                        Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED
                ),
                false
        );
    }

    @Override
    public @NotNull Iterator<Cell> iterator() {
        return new SliceCellIterator();
    }

    private class SliceCellIterator implements Iterator<Cell> {

        private boolean hasNext = true;
        private final int[] currentCoords;

        private SliceCellIterator() {
            this.currentCoords = new int[first.size()];
            for(int i=0; i< first.size(); ++i) {
                currentCoords[i] = first.coord(i);
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public @NotNull Cell next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }

            final Cell currentCell = Cell.of(currentCoords);

            advanceCoordinates();

            return currentCell;
        }

        private void advanceCoordinates() {
            for (int dim = first.size() - 1; dim >= 0; dim--) {
                currentCoords[dim]++;
                if (currentCoords[dim] <= last.coord(dim)) {
                    // нет переполнения, можно остановиться
                    return;
                } else {
                    // переполнение, сбрасываем текущую координату и переходим на следующий разряд
                    currentCoords[dim] = first.coord(dim);
                }
            }
            // если мы дошли сюда, значит, прошли все координаты
            hasNext = false;
        }
    }

    private int calculateTotalCellCount() {
        int result = 1;
        for (int i = 0; i < first.size(); i++) {
            result *= last.coord(i) - first.coord(i) + 1;
        }
        return result;
    }

}
