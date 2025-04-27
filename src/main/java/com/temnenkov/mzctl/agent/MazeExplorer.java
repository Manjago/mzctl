package com.temnenkov.mzctl.agent;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MazeExplorer {
    private final Maze maze;
    private final Random random;

    public MazeExplorer(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
    }

    /**
     * Проверка, что лабиринт connected
     *
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected() {
        return isConnected(maze.getRandomCell(random));
    }

    /**
     * Проверка, что лабиринт connected
     *
     * @param startCell комната, с которой начинаем
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected(@NotNull Cell startCell) {
        final Set<Cell> visited = new HashSet<>();

        dfs(startCell, visited);

        return visited.size() == maze.totalCellCount();
    }

    /**
     * Проверка, что лабиринт не содержит циклов
     * @return true, если лабиринт не содержит циклов, false в противном случае
     */
    public boolean isAcyclic() {
        final Set<Cell> visited = new HashSet<>();
        for (Cell cell : maze) {
            if (!visited.contains(cell) && hasCycle(cell, null, visited)) {
                return false; // если нашли цикл, сразу возвращаем false
            }
        }
        return true; // циклов нет, возвращаем true
    }

    private boolean hasCycle(@NotNull Cell current, @Nullable Cell parent, @NotNull Set<Cell> visited) {
        visited.add(current);

        for (Cell neighbor : maze.getAvailableNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                if (hasCycle(neighbor, current, visited)) {
                    return true;
                }
            } else if (!neighbor.equals(parent)) {
                // если сосед уже посещён и это не родитель, значит, нашли цикл
                return true;
            }
        }
        return false; // циклов не нашли
    }

    /**
     * Проверка, что лабиринт perfect (connected + acyclic)
     * @return true, если лабиринт perfect, false в противном случае
     */
    public boolean isPerfect() {
        return isConnected() && isAcyclic();
    }

    // Проверка, что количество проходов равно N-1 (для perfect maze)
    public boolean hasCorrectNumberOfPasses() {
        return false;
    }

// Дополнительные проверки (опционально)

    private void dfs(@NotNull Cell current, @NotNull Set<Cell> visited) {
        visited.add(current);

        for (Cell neighbor : maze.getAvailableNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }
}
