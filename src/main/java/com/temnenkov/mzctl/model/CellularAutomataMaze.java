package com.temnenkov.mzctl.model;

import java.util.stream.Stream;

public interface CellularAutomataMaze extends Iterable<Cell> {

    /**
     * Возвращает размерность и размеры лабиринта.
     *
     * @return MazeDim объект, содержащий информацию о размерах лабиринта
     */
    MazeDim getDimensions();

    /**
     * Проверяет, является ли клетка стеной.
     *
     * @param coords координаты клетки
     * @return true, если клетка — стена, иначе false
     */
    boolean isWall(int... coords);

    /**
     * Проверяет, является ли клетка стеной.
     *
     * @param cell клетка лабиринта
     * @return true, если клетка — стена, иначе false
     */
    boolean isWall(Cell cell);

    /**
     * Устанавливает состояние клетки.
     *
     * @param isWall true, если клетка должна стать стеной, иначе false
     * @param coords координаты клетки
     */
    void setWall(boolean isWall, int... coords);

    /**
     * Устанавливает состояние клетки.
     *
     * @param isWall true, если клетка должна стать стеной, иначе false
     * @param cell клетка лабиринта
     */
    void setWall(boolean isWall, Cell cell);

    /**
     * Возвращает количество соседей, являющихся стенами, для заданной клетки.
     *
     * @param coords координаты клетки
     * @return количество соседей-стен
     */
    int countWallNeighbors(int... coords);

    /**
     * Возвращает количество соседей, являющихся стенами, для заданной клетки.
     *
     * @param cell клетка лабиринта
     * @return количество соседей-стен
     */
    int countWallNeighbors(Cell cell);

    /**
     * Инициализирует лабиринт случайным образом.
     *
     * @param fillProbability вероятность того, что клетка станет стеной при инициализации
     * @return ссылку на этот же лабиринт (для fluent-интерфейса)
     */
    CellularAutomataMaze initialize(double fillProbability);

    /**
     * Все комнаты лабиринта (стены и нет)
     *
     * @return все комнаты лабиринта
     */
    Stream<Cell> stream();
}