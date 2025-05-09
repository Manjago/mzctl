package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class Facing {
    private final int[] direction;

    @JsonCreator
    public Facing(@JsonProperty("direction") int @NotNull [] direction) {
        if (direction.length == 0) {
            throw new IllegalArgumentException("Direction array must not be empty");
        }
        if (Arrays.stream(direction).allMatch(d -> d == 0)) {
            throw new IllegalArgumentException("Direction vector cannot be zero");
        }
        this.direction = direction.clone();
    }

    public static @NotNull Facing of(int... coords) {
        return new Facing(coords);
    }

    @Contract(value = " -> new", pure = true)
    public int[] getDirections() {
        return direction.clone(); // возвращаем копию для иммутабельности
    }

    public int size() {
        return direction.length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        Facing facing = (Facing) o;
        return Arrays.equals(direction, facing.direction);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(direction);
    }

    public @NotNull Cell moveForward(@NotNull Cell cell) {
        SimplePreconditions.checkState(cell.size() == this.size(), "Invalid cell size");
        final int[] result = new int[cell.size()];
        for (int i = 0; i < cell.size(); i++) {
            result[i] = cell.coord(i) + direction[i];
        }
        return Cell.of(result);
    }

    @Contract("_, _ -> new")
    public @NotNull Facing turn(int dimA, int dimB) {
        final int[] dir = getDirections();
        int temp = dir[dimA];
        dir[dimA] = -dir[dimB];
        dir[dimB] = temp;
        return new Facing(dir);
    }

    @Override
    public String toString() {
        return "Facing{" + "direction=" + Arrays.toString(direction) + '}';
    }
}