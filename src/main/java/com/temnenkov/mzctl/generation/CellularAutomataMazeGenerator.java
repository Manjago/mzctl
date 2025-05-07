package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.model.CellularAutomataMazeImpl;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CellularAutomataMazeGenerator {

    private static final String CTOR = ".ctor";
    private final double fillProbability;
    private final int iterations;
    private final double wallKeepThreshold;
    private final double emptyToWallThreshold;
    private final Random random;

    private static final double DEFAULT_WALL_KEEP_THRESHOLD = 0.5;
    private static final double DEFAULT_EMPTY_TO_WALL_THRESHOLD = 0.625;

    /**
     * Конструктор генератора лабиринтов на основе клеточных автоматов (с указанием всех параметров).
     *
     * @param fillProbability вероятность того, что клетка будет стеной при инициализации (обычно 0.4-0.45)
     * @param iterations      количество итераций клеточного автомата (обычно 4-5)
     * @param wallKeepThreshold порог для сохранения стены
     * @param emptyToWallThreshold порог для превращения пустой клетки в стену
     * @param random          генератор случайных чисел
     */
    public CellularAutomataMazeGenerator(double fillProbability, int iterations,
            double wallKeepThreshold, double emptyToWallThreshold,
            @NotNull Random random) {
        SimplePreconditions.checkState(fillProbability >= 0 && fillProbability <= 1,
                "fillProbability must be in [0, 1]");
        SimplePreconditions.checkState(iterations > 0,
                "iterations must be positive");
        SimplePreconditions.checkState(wallKeepThreshold >= 0 && wallKeepThreshold <= 1,
                "wallKeepThreshold must be in [0, 1]");
        SimplePreconditions.checkState(emptyToWallThreshold >= 0 && emptyToWallThreshold <= 1,
                "emptyToWallThreshold must be in [0, 1]");

        this.fillProbability = fillProbability;
        this.iterations = iterations;
        this.wallKeepThreshold = wallKeepThreshold;
        this.emptyToWallThreshold = emptyToWallThreshold;
        this.random = SimplePreconditions.checkNotNull(random, "random", CTOR);
    }

    /**
     * Перегруженный конструктор с порогами по умолчанию.
     *
     * @param fillProbability вероятность того, что клетка будет стеной при инициализации
     * @param iterations      количество итераций клеточного автомата
     * @param random          генератор случайных чисел
     */
    public CellularAutomataMazeGenerator(double fillProbability, int iterations, @NotNull Random random) {
        this(fillProbability, iterations,
                DEFAULT_WALL_KEEP_THRESHOLD, DEFAULT_EMPTY_TO_WALL_THRESHOLD,
                random);
    }

    /**
     * Генерирует лабиринт с помощью клеточных автоматов.
     *
     * @param dim размерность лабиринта
     * @return сгенерированный лабиринт
     */
    public CellularAutomataMaze generate(@NotNull MazeDim dim) {
        SimplePreconditions.checkNotNull(dim, "dim", "generate");

        final CellularAutomataMaze maze = new CellularAutomataMazeImpl(dim, random)
                .initialize(fillProbability);

        for (int i = 0; i < iterations; i++) {
            simulationStep(maze);
        }

        return maze;
    }

    /**
     * Выполняет один шаг симуляции клеточного автомата.
     *
     * @param maze текущий лабиринт
     */
    private void simulationStep(@NotNull CellularAutomataMaze maze) {
        final CellularAutomataMaze nextMaze = new CellularAutomataMazeImpl(maze.getDimensions(), random);

        maze.stream().forEach(cell -> {
            final int wallCount = maze.countWallNeighbors(cell);
            final int totalNeighbors = (int) Math.pow(3, maze.getDimensions().size()) - 1;
            double wallRatio = (double) wallCount / totalNeighbors;

            if (maze.isWall(cell)) {
                nextMaze.setWall(wallRatio >= wallKeepThreshold, cell);
            } else {
                nextMaze.setWall(wallRatio >= emptyToWallThreshold, cell);
            }
        });

        maze.stream().forEach(cell -> maze.setWall(nextMaze.isWall(cell), cell));
    }
}