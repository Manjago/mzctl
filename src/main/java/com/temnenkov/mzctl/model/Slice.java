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
            throw new IllegalArgumentException("First and last cells must have the same dimensions");
        }
        if (!isFirstNotGreaterThanLast(first, last)) {
            throw new IllegalArgumentException("Each coordinate of the first cell must not be greater than the corresponding coordinate of the last cell");
        }
        this.first = first;
        this.last = last;
        this.totalCellCount = calculateTotalCellCount();
    }

    private boolean isFirstNotGreaterThanLast(@NotNull Cell first, @NotNull Cell last) {
        for(int i=0; i < first.size(); ++i) {
            if (first.coord(i) > last.coord(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Возвращает общее количество ячеек в данном срезе.
     *
     * @return число ячеек
     */
    public int getTotalCellCount() {
        return totalCellCount;
    }

    /**
     * Создает поток (stream) из всех ячеек, входящих в текущий срез.
     *
     * @return поток ячеек
     */
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
                    /// Успешно увеличили текущую координату, выходим из цикла
                    return;
                } else {
                    // Текущая координата превысила допустимую, сбрасываем её и переходим к следующей координате
                    currentCoords[dim] = first.coord(dim);
                }
            }
            // Если мы здесь, значит, прошли все возможные координаты
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        Slice cells = (Slice) o;
        return first.equals(cells.first) && last.equals(cells.last);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + last.hashCode();
        return result;
    }
}
