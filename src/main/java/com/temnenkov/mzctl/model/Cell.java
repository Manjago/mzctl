package com.temnenkov.mzctl.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Ячейка (комната) в лабиринте. Лабиринт может быть многомерным, соответственно, храним массив координат.
 */
@SuppressWarnings("java:S6206")
public final class Cell {
    private final int[] coordinates;

    @JsonCreator
    public Cell(@JsonProperty("coordinates") int[] coordinates) {
        if (coordinates.length == 0) {
            throw new IllegalArgumentException("Coordinates array must not be empty");
        }
        this.coordinates = coordinates.clone();
    }

    public static @NotNull Cell of(int... coords) {
        return new Cell(coords);
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