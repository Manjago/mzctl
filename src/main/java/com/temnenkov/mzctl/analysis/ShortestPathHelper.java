package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Вспомогательный класс для поиска кратчайшего расстояния между двумя ячейками в лабиринте.
 */
public final class ShortestPathHelper {
    private ShortestPathHelper() {
    }

    /**
     * Вычисляет кратчайшее расстояние (количество шагов) между двумя ячейками в лабиринте.
     * Использует Breadth-First Search (BFS).
     *
     * @param maze  лабиринт, в котором вычисляется расстояние
     * @param start начальная ячейка
     * @param end   конечная ячейка
     * @return кратчайшее расстояние между ячейками, или -1, если путь недостижим
     */
    public static int shortestDistance(@NotNull Maze maze, @NotNull Cell start, @NotNull Cell end) {
        if (start.equals(end)) {
            return 0;
        }

        final Queue<Cell> queue = new ArrayDeque<>();
        final Map<Cell, Integer> distances = new HashMap<>();
        queue.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            final Cell current = queue.poll();
            if (current.equals(end)) {
                return distances.get(current);
            }
            for (Cell neighbor : maze.getAvailableNeighbors(current)) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, distances.get(current) + 1);
                    queue.add(neighbor);
                }
            }
        }
        return -1; // должно быть недостижимо в connected maze
    }
}
