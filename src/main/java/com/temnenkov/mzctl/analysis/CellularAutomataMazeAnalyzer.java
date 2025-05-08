package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class CellularAutomataMazeAnalyzer {

    @NotNull
    private final CellularAutomataMaze maze;
    private final Set<Cell> visited = new HashSet<>();

    public CellularAutomataMazeAnalyzer(@NotNull CellularAutomataMaze maze) {
        this.maze = maze;
    }

    /**
     * Возвращает процент клеток-стен в лабиринте.
     *
     * @return процент стен (от 0.0 до 1.0)
     */
    public double wallPercentage() {
        final long totalCells = maze.totalCells();
        SimplePreconditions.checkState(totalCells > 0, "Total cells must be greater than zero - programmer bug");
        final long wallCells = maze.stream().filter(maze::isWall).count();
        SimplePreconditions.checkState(wallCells <= totalCells, "Wall cells must be less than total cells - programmer bug");
        return (double) wallCells / totalCells;
    }

    /**
     * Возвращает количество изолированных областей пустых клеток.
     *
     * @return количество изолированных областей
     */
    public int countIsolatedAreas() {
        visited.clear();
        int count = 0;

        for (Cell cell : maze) {
            if (!maze.isWall(cell) && !visited.contains(cell)) {
                bfs(cell);
                count++;
            }
        }

        return count;
    }

    /**
     * Возвращает размер самой большой связной области пустых клеток.
     *
     * @return размер самой большой области
     */
    public int largestAreaSize() {
        visited.clear();
        int maxSize = 0;

        for (Cell cell : maze) {
            if (!maze.isWall(cell) && !visited.contains(cell)) {
                final int size = bfs(cell);
                maxSize = Math.max(maxSize, size);
            }
        }

        return maxSize;
    }

    /**
     * Алгоритм поиска в ширину (BFS) для обхода области.
     *
     * @param startCell начальная клетка
     * @return размер области
     */
    private int bfs(Cell startCell) {
        int areaSize = 0;
        final Queue<Cell> queue = new ArrayDeque<>();
        queue.add(startCell);
        visited.add(startCell);

        while (!queue.isEmpty()) {
            final Cell current = queue.poll();
            areaSize++;

            current.neighbors()
                    .filter(maze::isValid)
                    .filter(neighbor -> !maze.isWall(neighbor))
                    .filter(neighbor -> !visited.contains(neighbor))
                    .forEach(neighbor -> {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    });
        }

        return areaSize;
    }
}