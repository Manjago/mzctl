package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        this.direction = direction.clone();
    }

    public static @NotNull Facing of(int... coords) {
        return new Facing(coords);
    }

    @Contract(value = " -> new", pure = true)
    public int[] getDirections() {
        return direction.clone(); // возвращаем копию для иммутабельности
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

    @Override
    public String toString() {
        return "Facing{" + "direction=" + Arrays.toString(direction) + '}';
    }
}
