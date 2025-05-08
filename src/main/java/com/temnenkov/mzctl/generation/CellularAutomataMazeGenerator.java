package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.model.CellularAutomataMazeImpl;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.util.SimpleMath;
import com.temnenkov.mzctl.util.SimplePreconditions;
import com.temnenkov.mzctl.visualization.MazeVisualizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CellularAutomataMazeGenerator {

    private static final String CTOR = ".ctor";
    private final double fillProbability;
    private final int iterations;
    private final double wallKeepThreshold;
    private final double emptyToWallThreshold;
    private final Random random;
    private final MazeVisualizer visualizer;

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
     * @param visualizer      опциональный визуализатор лабиринта (может быть null)
     */
    public CellularAutomataMazeGenerator(double fillProbability, int iterations,
            double wallKeepThreshold, double emptyToWallThreshold,
            @NotNull Random random, @Nullable MazeVisualizer visualizer) {
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
        this.visualizer = visualizer;
    }

    /**
     * Перегруженный конструктор с порогами по умолчанию и без визуализатора.
     *
     * @param fillProbability вероятность того, что клетка будет стеной при инициализации
     * @param iterations      количество итераций клеточного автомата
     * @param random          генератор случайных чисел
     */
    public CellularAutomataMazeGenerator(double fillProbability, int iterations, @NotNull Random random) {
        this(fillProbability, iterations,
                DEFAULT_WALL_KEEP_THRESHOLD, DEFAULT_EMPTY_TO_WALL_THRESHOLD,
                random, null);
    }

    /**
     * Генерирует лабиринт с помощью клеточных автоматов.
     *
     * @param dim размерность лабиринта
     * @return сгенерированный лабиринт
     */
    public CellularAutomataMaze generate(@NotNull MazeDim dim) {
        SimplePreconditions.checkNotNull(dim, "dim", "generate");

        CellularAutomataMaze maze = new CellularAutomataMazeImpl(dim, random)
                .initialize(fillProbability);

        if (visualizer != null) {
            visualizer.visualize(maze, 0);
        }

        for (int i = 0; i < iterations; i++) {
            maze = simulationStep(maze);

            if (visualizer != null) {
                visualizer.visualize(maze, i + 1);
            }
        }

        return maze;
    }

    /**
     * Выполняет один шаг симуляции клеточного автомата.
     *
     * @param maze текущий лабиринт
     * @return лабиринт на следующем шаге
     */
    private @NotNull CellularAutomataMaze simulationStep(@NotNull CellularAutomataMaze maze) {
        final CellularAutomataMaze nextMaze = new CellularAutomataMazeImpl(maze.getDimensions(), random);

        final int totalNeighbors = SimpleMath.pow(3, maze.getDimensions().size()) - 1;

        maze.stream().forEach(cell -> {
            final int wallCount = maze.countWallNeighbors(cell);
            final double wallRatio = (double) wallCount / totalNeighbors;

            if (maze.isWall(cell)) {
                nextMaze.setWall(wallRatio >= wallKeepThreshold, cell);
            } else {
                nextMaze.setWall(wallRatio >= emptyToWallThreshold, cell);
            }
        });

        return nextMaze;
    }
}