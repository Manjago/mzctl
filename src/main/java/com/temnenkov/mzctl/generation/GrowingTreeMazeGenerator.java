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

public class GrowingTreeMazeGenerator implements MazeGenerator {

    private static final String CTOR = ".ctor";

    public enum Strategy {
        NEWEST, RANDOM, OLDEST, MIXED
    }

    private final Maze maze;
    private final Random random;
    private final Strategy strategy;
    private final double mixedProbability; // для MIXED стратегии
    private boolean generated = false;
    private final Set<Cell> visited = new HashSet<>();

    public GrowingTreeMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random, @NotNull Strategy strategy, double mixedProbability) {
        this.maze = MazeFactory.createNotConnectedMaze(SimplePreconditions.checkNotNull(mazeDim, "mazeDim", CTOR));
        this.random = SimplePreconditions.checkNotNull(random ,"random", CTOR);
        this.strategy = SimplePreconditions.checkNotNull(strategy, "strategy", CTOR);
        this.mixedProbability = mixedProbability;
        if(strategy == Strategy.MIXED) {
            SimplePreconditions.checkState(
                    mixedProbability >= 0.0 && mixedProbability <= 1.0,
                    "mixedProbability must be between 0 and 1");
        }
    }

    @Override
    public Maze generateMaze() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;

        final IndexedHashSet<Cell> activeCells = new IndexedHashSet<>();

        // Начинаем с произвольной начальной ячейки
        final Cell start = maze.getRandomCell(random);
        activeCells.add(start);
        visited.add(start);

        while (!activeCells.isEmpty()) {
            final Cell current;

            // Выбор следующей ячейки согласно стратегии
            switch(strategy) {
                case NEWEST:
                    current = activeCells.getLast();
                    break;
                case OLDEST:
                    current = activeCells.getFirst();
                    break;
                case RANDOM:
                    current = activeCells.getRandom(random);
                    break;
                case MIXED:
                    if(random.nextDouble() < mixedProbability) {
                        current = activeCells.getLast();
                    } else {
                        current = activeCells.getRandom(random);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown strategy: " + strategy);
            }

            // Получаем непосещённых соседей
            final List<Cell> unvisitedNeighbors = getUnvisitedNeighbors(current);

            if (!unvisitedNeighbors.isEmpty()) {
                // Выбираем случайного соседа
                final Cell neighbor = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));

                // Соединяем текущую ячейку с соседом
                maze.addPass(current, neighbor);

                // Помечаем соседа посещённым и добавляем в активный набор
                visited.add(neighbor);
                activeCells.add(neighbor);
            } else {
                // Если соседей нет — удаляем текущую ячейку из активного набора
                activeCells.remove(current);
            }
        }

        return maze;
    }

    private List<Cell> getUnvisitedNeighbors(@NotNull Cell cell) {
        return maze.getAllNeighbors(cell).filter(c -> !visited.contains(c)).toList();
    }
}