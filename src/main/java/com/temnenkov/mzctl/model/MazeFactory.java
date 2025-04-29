package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Фабрика для создания различных типов лабиринтов.
 */
public final class MazeFactory {

    private MazeFactory() {
        // скрытый конструктор, чтобы не создавать экземпляры
    }

    /**
     * Создаёт лабиринт без проходов (все комнаты изолированы).
     *
     * @param mazeDimension размерность лабиринта
     * @return лабиринт без проходов
     */
    @NotNull
    public static Maze createNotConnectedMaze(@NotNull MazeDim mazeDimension) {
        return new Maze(mazeDimension);
    }

    /**
     * Создаёт лабиринт без проходов (все комнаты изолированы).
     *
     * @param dimensions размеры лабиринта по каждому измерению
     * @return лабиринт без проходов
     */
    @NotNull
    public static Maze createNotConnectedMaze(int... dimensions) {
        return new Maze(MazeDim.of(dimensions));
    }

    /**
     * Создаёт полностью соединённый лабиринт (все соседние клетки соединены).
     *
     * @param mazeDimension размерность лабиринта
     * @return полностью соединённый лабиринт
     */
    @NotNull
    public static Maze createFullConnectedMaze(@NotNull MazeDim mazeDimension) {
        final Maze maze = createNotConnectedMaze(mazeDimension);
        return connectAll(maze);
    }

    /**
     * Создаёт полностью соединённый лабиринт (все соседние клетки соединены).
     *
     * @param dimensions размеры лабиринта по каждому измерению
     * @return полностью соединённый лабиринт
     */
    @NotNull
    public static Maze createFullConnectedMaze(int... dimensions) {
        final Maze maze = createNotConnectedMaze(dimensions);
        return connectAll(maze);
    }

    @Contract("_ -> param1")
    private static @NotNull Maze connectAll(@NotNull Maze maze) {
        for(Cell cell: maze) {
            final Set<Cell> neighbours = maze.getAllNeighbors(cell).collect(Collectors.toSet());
            maze.addPass(cell, neighbours);
        }
        return maze;
    }
}