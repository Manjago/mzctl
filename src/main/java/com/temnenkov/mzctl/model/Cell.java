package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ячейка (комната) в лабиринте. Лабиринт может быть многомерным, соответственно, храним список координат
 *
 * @param coordinates список координат по каждому измерению
 */
public record Cell(@NotNull List<Integer> coordinates) {

    /**
     * Инициализируем прямо списком (при инициализации копируем ради иммутабельности)
     *
     * @param coordinates список, структура такая же, как во внутреннем представлении
     */
    public Cell(@NotNull List<Integer> coordinates) {
        if (coordinates.isEmpty()) {
            throw new IllegalArgumentException("Coordinates list must not be empty");
        }
        this.coordinates = List.copyOf(coordinates);
    }

    /**
     * Удобный метод для задания по явному перечислению координат
     *
     * @param coords координат
     * @return проинициализированная ячейка (комната)
     */
    public static @NotNull Cell of(int... coords) {
        return new Cell(Arrays.stream(coords).boxed().toList());
    }

    /**
     * Смещаемся на 1 "назад" по измерению dimension
     *
     * @param dimension номер измерения
     * @return ячейка с новыми координатами (внимание, она может выходить за границы лабиринта!)
     */
    @Contract("_ -> new")
    public @NotNull Cell minusOne(int dimension) {
        return plus(dimension, -1);
    }

    /**
     * Смещаемся на 1 "вперед" по измерению dimension
     *
     * @param dimension номер измерения
     * @return ячейка с новыми координатами (внимание, она может выходить за границы лабиринта!)
     */
    @Contract("_ -> new")
    public @NotNull Cell plusOne(int dimension) {
        return plus(dimension, +1);
    }

    /**
     * Смещаемся на inc по измерению dimension
     *
     * @param dimension номер измерения
     * @param inc       на сколько смещаемся (число со знаком)
     * @return ячейка с новыми координатами (внимание, она может выходить за границы лабиринта!)
     */
    @Contract("_, _ -> new")
    public @NotNull Cell plus(int dimension, int inc) {
        if (dimension < 0 || dimension >= coordinates.size()) {
            throw new IndexOutOfBoundsException("Invalid dimension index: " + dimension);
        }
        final List<Integer> newCoordinates = new ArrayList<>(coordinates);
        newCoordinates.set(dimension, coordinates.get(dimension) + inc);
        return new Cell(newCoordinates);
    }

    /**
     * Получить координату по измерению dimension
     *
     * @param dimension номер измерения
     * @return координата по измерению dimension
     */
    public int coord(int dimension) {
        if (dimension < 0 || dimension >= coordinates.size()) {
            throw new IndexOutOfBoundsException("Invalid dimension index: " + dimension);
        }
        return coordinates.get(dimension);
    }

    public int size() {
        return coordinates.size();
    }
}
