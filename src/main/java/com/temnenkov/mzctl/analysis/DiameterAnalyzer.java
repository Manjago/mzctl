package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class DiameterAnalyzer {
    private final Maze maze;
    private final Random random;

    public DiameterAnalyzer(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
    }

    public int diameter() {

        // первый BFS от произвольной клетки
        final Cell start = maze.getRandomCell(random);
        Cell farthestA = bfsFarthestCell(start);

        // второй BFS от найденной клетки farthestA
        Cell farthestB = bfsFarthestCell(farthestA);

        // возвращаем расстояние между farthestA и farthestB
        return ShortestPathHelper.shortestDistance(maze, farthestA, farthestB);
    }

    // вспомогательный метод: BFS и поиск самой удалённой клетки от start
    @NotNull
    private Cell bfsFarthestCell(@NotNull Cell start) {
        final Queue<Cell> queue = new ArrayDeque<>();
        final Set<Cell> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        Cell farthest = start;

        while (!queue.isEmpty()) {
            final Cell current = queue.poll();
            farthest = current; // последняя обработанная клетка будет самой дальней

            for (Cell neighbor : maze.getAvailableNeighbors(current)) {
                if (visited.add(neighbor)) { // add возвращает true, если клетка ещё не была посещена
                    queue.add(neighbor);
                }
            }
        }
        return farthest;
    }

}
