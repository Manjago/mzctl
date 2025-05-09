package com.temnenkov.mzctl.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.util.SimpleMath;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Ячейка (комната) в лабиринте. Лабиринт может быть многомерным, соответственно, храним массив координат.
 */
@SuppressWarnings("java:S6206")
public final class Cell implements Iterable<Cell> {
    private final int[] coordinates;
    private final int totalMeWithNe;

    @JsonCreator
    public Cell(@JsonProperty("coordinates") int[] coordinates) {
        if (coordinates.length == 0) {
            throw new IllegalArgumentException("Coordinates array must not be empty");
        }
        this.coordinates = coordinates.clone();
        this.totalMeWithNe = calculateTotalNeighboursAndMeCount();
    }

    public static @NotNull Cell of(int... coords) {
        return new Cell(coords);
    }

    public static @NotNull Cell ofColumnAndRow(int column, int row) {
        return  Cell.of(column, row);
    }

    @Contract("_ -> new")
    public @NotNull Cell minusOne(int dimension) {
        return plus(dimension, -1);
    }

    @Contract("_ -> new")
    public @NotNull Cell plusOne(int dimension) {
        return plus(dimension, +1);
    }

    public @NotNull Cell withNewDimensionValue(int dimensionNum, int dimensionValue) {
        checkDimensionIndex(dimensionNum);
        final int[] modifiedCoordinates = coordinates.clone();
        modifiedCoordinates[dimensionNum] = dimensionValue;
        return new Cell(modifiedCoordinates);
    }

    @Contract("_, _ -> new")
    public @NotNull Cell plus(int dimension, int inc) {
        checkDimensionIndex(dimension);
        final int[] newCoordinates = coordinates.clone();
        newCoordinates[dimension] += inc;
        return new Cell(newCoordinates);
    }

    public int coord(int dimension) {
        checkDimensionIndex(dimension);
        return coordinates[dimension];
    }

    public int size() {
        return coordinates.length;
    }

    @Contract(value = " -> new", pure = true)
    public int[] getCoordinates() {
        return coordinates.clone(); // возвращаем копию для иммутабельности
    }

    private void checkDimensionIndex(int dimension) {
        if (dimension < 0 || dimension >= coordinates.length) {
            throw new IndexOutOfBoundsException("Invalid dimension index: " + dimension);
        }
    }

    @Contract(" -> new")
    public @NotNull Stream<Cell> neighborsAndSelf() {
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iterator(),
                        totalMeWithNe,
                        Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED
                ),
                false
        );
    }

    @Contract(" -> new")
    public @NotNull Stream<Cell> neighbors() {
        return neighborsAndSelf().filter(c -> !c.equals(this));
    }

    @Override
    public @NotNull Iterator<Cell> iterator() {
        return new CellWithNeighboursIterator(this);
    }

    private static class CellWithNeighboursIterator implements Iterator<Cell> {

        private final Cell origin;

        private boolean hasNext = true;
        private final int[] currentCoords;

        private CellWithNeighboursIterator(@NotNull Cell origin) {
            this.origin = origin;
            this.currentCoords = origin.getCoordinates();
            for(int i = 0; i<currentCoords.length; i++) {
                currentCoords[i]--;
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
            for (int dim = currentCoords.length - 1; dim >= 0; dim--) {
                currentCoords[dim]++;
                if (currentCoords[dim] <= origin.coord(dim) + 1) {
                    // нет переполнения, можно остановиться
                    return;
                } else {
                    // переполнение, сбрасываем текущую координату и переходим на следующий разряд
                    currentCoords[dim] = origin.coord(dim) - 1;
                }
            }
            // если мы дошли сюда, значит, прошли все координаты
            hasNext = false;
        }
    }

    private int calculateTotalNeighboursAndMeCount() {
        return SimpleMath.pow(3, coordinates.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell cell)) return false;
        return Arrays.equals(coordinates, cell.coordinates);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates);
    }

    @Override
    public String toString() {
        return "Cell" + Arrays.toString(coordinates);
    }
}