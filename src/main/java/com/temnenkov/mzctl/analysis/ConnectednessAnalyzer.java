package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ConnectednessAnalyzer {

    private final Maze maze;
    private final Random random;

    public ConnectednessAnalyzer(Maze maze, Random random) {
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

    private void dfs(@NotNull Cell current, @NotNull Set<Cell> visited) {
        visited.add(current);

        for (Cell neighbor : maze.getAvailableNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }

}
