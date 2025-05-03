package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.IndexedHashSet;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Генератор лабиринтов, реализующий алгоритм Growing Tree.
 * Поддерживает различные стратегии выбора следующей ячейки.
 */
public class GrowingTreeMazeGenerator implements MazeGenerator {

    private static final String CTOR = ".ctor";

    /**
     * Стратегия выбора следующей ячейки из списка активных ячеек.
     */
    public enum Strategy {
        /** Последняя добавленная ячейка (аналог Recursive Backtracker). */
        NEWEST,

        /** Случайная ячейка (аналог Randomized Prim). */
        RANDOM,

        /** Самая первая добавленная ячейка (аналог обхода в ширину). */
        OLDEST,

        /** Смешанная стратегия (NEWEST с вероятностью mixedProbability, иначе RANDOM). */
        MIXED
    }

    private final Maze maze;
    private final Random random;
    private final Strategy strategy;
    private final double mixedProbability;
    private boolean generated = false;
    private final Set<Cell> visited = new HashSet<>();

    /**
     * Конструктор генератора Growing Tree.
     *
     * @param mazeDim размерность лабиринта
     * @param random источник случайности
     * @param strategy стратегия выбора следующей ячейки
     * @param mixedProbability вероятность выбора NEWEST в MIXED стратегии (от 0 до 1)
     */
    public GrowingTreeMazeGenerator(@NotNull MazeDim mazeDim,
            @NotNull Random random,
            @NotNull Strategy strategy,
            double mixedProbability) {
        this.maze = MazeFactory.createNotConnectedMaze(SimplePreconditions.checkNotNull(mazeDim, "mazeDim", CTOR));
        this.random = SimplePreconditions.checkNotNull(random, "random", CTOR);
        this.strategy = SimplePreconditions.checkNotNull(strategy, "strategy", CTOR);
        this.mixedProbability = mixedProbability;

        if (strategy == Strategy.MIXED) {
            SimplePreconditions.checkState(
                    mixedProbability >= 0.0 && mixedProbability <= 1.0,
                    "mixedProbability must be between 0 and 1");
        }
    }

    /**
     * Генерирует лабиринт согласно выбранной стратегии.
     *
     * @return сгенерированный лабиринт
     */
    @Override
    public Maze generateMaze() {
        checkAlreadyGenerated();

        final IndexedHashSet<Cell> activeCells = new IndexedHashSet<>();

        // Начинаем с произвольной стартовой ячейки
        final Cell start = maze.getRandomCell(random);
        activeCells.add(start);
        visited.add(start);

        while (!activeCells.isEmpty()) {
            final Cell current = selectNextCell(activeCells);
            final List<Cell> unvisitedNeighbors = getUnvisitedNeighbors(current);

            if (!unvisitedNeighbors.isEmpty()) {
                final Cell neighbor = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                maze.addPass(current, neighbor);
                visited.add(neighbor);
                activeCells.add(neighbor);
            } else {
                activeCells.remove(current);
            }
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

    /**
     * Выбирает следующую ячейку из активного набора согласно текущей стратегии.
     *
     * @param activeCells активный набор ячеек
     * @return выбранная ячейка
     */
    private Cell selectNextCell(@NotNull IndexedHashSet<Cell> activeCells) {
        return switch (strategy) {
            case NEWEST -> activeCells.getLast();
            case OLDEST -> activeCells.getFirst();
            case RANDOM -> activeCells.getRandom(random);
            case MIXED -> random.nextDouble() < mixedProbability
                    ? activeCells.getLast()
                    : activeCells.getRandom(random);
        };
    }

    /**
     * Возвращает список непосещённых соседей для данной ячейки.
     *
     * @param cell текущая ячейка
     * @return список непосещённых соседей
     */
    private List<Cell> getUnvisitedNeighbors(@NotNull Cell cell) {
        return maze.getAllNeighbors(cell)
                .filter(c -> !visited.contains(c))
                .toList();
    }
}