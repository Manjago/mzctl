package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Генератор лабиринтов по алгоритму Aldous-Broder.
 * Генерирует лабиринты с равномерным распределением (каждый лабиринт равновероятен).
 */
public class AldousBroderMazeGenerator implements MazeGenerator {

    private static final String CTOR = ".ctor";

    private final Maze maze;
    private final Random random;
    private boolean generated = false;
    private final Set<Cell> visited = new HashSet<>();

    /**
     * Конструктор генератора Aldous-Broder.
     *
     * @param mazeDim размерность лабиринта
     * @param random  источник случайности
     */
    public AldousBroderMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.maze = MazeFactory.createNotConnectedMaze(SimplePreconditions.checkNotNull(mazeDim, "mazeDim", CTOR));
        this.random = SimplePreconditions.checkNotNull(random, "random", CTOR);
    }

    @Override
    public Maze generateMaze() {
        checkAlreadyGenerated();

        final int totalCells = maze.totalCellCount();
        Cell currentCell = maze.getRandomCell(random);
        visited.add(currentCell);

        while (visited.size() < totalCells) {
            final List<Cell> neighbors = maze.getAllNeighbors(currentCell).toList();
            final Cell neighbor = neighbors.get(random.nextInt(neighbors.size()));

            if (!visited.contains(neighbor)) {
                maze.addPass(currentCell, neighbor);
                visited.add(neighbor);
            }

            currentCell = neighbor;
        }

        return maze;
    }

    /**
     * Проверяет, не был ли лабиринт уже сгенерирован.
     * Если лабиринт был уже сгенерирован, выбрасывает исключение.
     */
    private void checkAlreadyGenerated() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;
    }
}