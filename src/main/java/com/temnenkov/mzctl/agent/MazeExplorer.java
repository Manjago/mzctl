package com.temnenkov.mzctl.agent;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

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
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected() {
        return isConnected(maze.getRandomCell(random));
    }

    /**
     * Проверка, что лабиринт connected
     * @param startCell комната, с которой начинаем
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected(@NotNull Cell startCell) {
        final Set<Cell> visited = new HashSet<>();

        dfs(startCell, visited);

        return visited.size() == maze.totalCellCount();
    }

    // Проверка, что лабиринт не содержит циклов
    public boolean isAcyclic() {
        return false;
    }

    // Проверка, что лабиринт perfect (connected + acyclic)
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
