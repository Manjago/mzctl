package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Размерность массива
 * Число элементов списка lenData - количество измерений, значение по индексу - размер по измерению
 *
 * @param lenData количество клеток по измерению
 */
public record MazeDim(@NotNull List<Integer> lenData) {

    /**
     * Инициализируем прямо списком (при инициализации копируем ради иммутабельности)
     *
     * @param lenData список, структура такая же, как во внутреннем представлении
     */
    public MazeDim(@NotNull List<Integer> lenData) {
        if (lenData.isEmpty()) {
            throw new IllegalArgumentException("Dimension data must not be empty");
        }
        if (lenData.stream().anyMatch(size -> size <= 0)) {
            throw new IllegalArgumentException("All dimension sizes must be positive");
        }
        this.lenData = List.copyOf(lenData);
    }

    /**
     * Удобный метод для задания по явному перечислению dimensions
     *
     * @param dimensions измерения
     * @return проинициализированное хранилище измерений
     */
    public static @NotNull MazeDim of(int... dimensions) {
        return new MazeDim(Arrays.stream(dimensions).boxed().toList());
    }

    /**
     * Количество размерностей
     *
     * @return целое число количество размерностей
     */
    public int size() {
        return lenData.size();
    }

    /**
     * Максимальный размер по измерению
     *
     * @param num номер измерения
     * @return число - максимальный размер по измерению
     */
    public int dimSize(int num) {
        if (num < 0 || num >= lenData.size()) {
            throw new IndexOutOfBoundsException("Invalid dimension index: " + num);
        }
        return lenData.get(num);
    }

    /**
     * Получить список размеров по всем измерениям
     *
     * @return список размеров
     */
    public List<Integer> dimensions() {
        return lenData;
    }

    public String display() {
        return lenData.stream().map(String::valueOf).collect(Collectors.joining("x"));
    }

}
