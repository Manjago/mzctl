package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class Facing {
    private final AxisDirection[] direction;

    /**
     * Перечисление для направлений вдоль оси.
     */
    public enum AxisDirection {
        NEGATIVE(-1),
        ZERO(0),
        POSITIVE(1);

        private final int value;

        AxisDirection(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        @JsonCreator
        public static AxisDirection fromValue(int value) {
            return switch (value) {
                case -1 -> NEGATIVE;
                case 0 -> ZERO;
                case 1 -> POSITIVE;
                default -> throw new IllegalArgumentException("Invalid axis direction value: " + value);
            };
        }
    }

    /**
     * Перечисление осей измерений.
     */
    public enum Dimension {
        X(0),
        Y(1),
        Z(2),
        W(3); // Можно добавлять по необходимости

        private final int index;

        Dimension(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }

    @JsonCreator
    private Facing(@JsonProperty("direction") AxisDirection @NotNull [] direction) {
        SimplePreconditions.checkArgument(direction.length > 0, "Direction array must not be empty");
        SimplePreconditions.checkArgument(Arrays.stream(direction).anyMatch(d -> d != AxisDirection.ZERO), "Direction vector cannot be zero");
        this.direction = direction.clone();
    }

    @Contract("_ -> new")
    public static @NotNull Facing of(AxisDirection @NotNull ... directions) {
        SimplePreconditions.checkArgument(directions.length > 0, "Directions array must not be empty");
        return new Facing(directions);
    }

    @Contract("-> new")
    public @NotNull Facing opposite() {
        final AxisDirection[] oppositeDirections = new AxisDirection[direction.length];
        for (int i = 0; i < direction.length; i++) {
            oppositeDirections[i] = AxisDirection.fromValue(-direction[i].value());
        }
        return new Facing(oppositeDirections);
    }

    @Contract(value = " -> new", pure = true)
    public AxisDirection[] getDirections() {
        return direction.clone(); // возвращаем копию для иммутабельности
    }

    public int size() {
        return direction.length;
    }

    /**
     * Двигает клетку вперед в текущем направлении.
     *
     * @param cell текущая клетка
     * @return новая клетка после движения
     */
    public @NotNull Cell moveForward(@NotNull Cell cell) {
        SimplePreconditions.checkState(cell.size() == this.size(), "Invalid cell size");
        final int[] result = new int[cell.size()];
        for (int i = 0; i < cell.size(); i++) {
            result[i] = cell.coord(i) + direction[i].value();
        }
        return Cell.of(result);
    }

    /**
     * Поворачивает направление взгляда в плоскости, заданной двумя измерениями.
     *
     * @param dimA первое измерение плоскости
     * @param dimB второе измерение плоскости
     * @return новое направление взгляда после поворота
     */
    @Contract("_, _ -> new")
    public @NotNull Facing turn(Dimension dimA, Dimension dimB) {
        validateDimensions(dimA, dimB);

        final AxisDirection[] dir = getDirections();
        AxisDirection temp = dir[dimA.index()];
        dir[dimA.index()] = AxisDirection.fromValue(-dir[dimB.index()].value());
        dir[dimB.index()] = temp;
        return new Facing(dir);
    }

    /**
     * Поворот налево в двумерном лабиринте.
     */
    public @NotNull Facing turnLeft2D() {
        SimplePreconditions.checkState(direction.length == 2, "Only 2D directions are supported");
        return turn(Dimension.X, Dimension.Y);
    }

    /**
     * Поворот направо в двумерном лабиринте.
     */
    public @NotNull Facing turnRight2D() {
        SimplePreconditions.checkState(direction.length == 2, "Only 2D directions are supported");
        return turn(Dimension.Y, Dimension.X);
    }

    private void validateDimensions(@NotNull Dimension dimA, @NotNull Dimension dimB) {
        SimplePreconditions.checkArgument(dimA != dimB, "Dimensions must be different");
        SimplePreconditions.checkArgument(dimA.index() < size() && dimB.index() < size(), "Dimension indexes must be within direction size");
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
        final StringBuilder sb = new StringBuilder("Facing[");
        boolean first = true;
        for (int i = 0; i < direction.length; i++) {
            if (direction[i] != AxisDirection.ZERO) {
                if (!first) sb.append(' ');
                sb.append(Dimension.values()[i].name())
                        .append(direction[i] == AxisDirection.POSITIVE ? "(+)" : "(-)");
                first = false;
            }
        }
        sb.append(']');
        return sb.toString();
    }
}