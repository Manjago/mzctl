package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class ConnectednessAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(ConnectednessAnalyzer.class);

    private final Maze maze;
    private final Random random;

    public ConnectednessAnalyzer(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
    }

    /**
     * Проверяет, является ли лабиринт полностью связным.
     *
     * @return true, если лабиринт connected, false иначе
     */
    public boolean isConnected() {
        return isConnected(maze.getRandomCell(random));
    }

    /**
     * Проверяет, является ли лабиринт полностью связным, начиная с указанной ячейки.
     *
     * @param startCell начальная ячейка для обхода
     * @return true, если лабиринт connected, false иначе
     */
    public boolean isConnected(@NotNull Cell startCell) {
        final Set<Cell> visited = new HashSet<>();
        final Queue<Cell> queue = new ArrayDeque<>();

        queue.add(startCell);
        visited.add(startCell);

        int steps = 0;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            steps++;

            for (Cell neighbor : maze.getAvailableNeighbors(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        final boolean connected = visited.size() == maze.totalCellCount();

        if (logger.isTraceEnabled()) {
            logger.trace("Maze connectivity check completed: connected={}, total steps={}, visited cells={}/{}",
                    connected, steps, visited.size(), maze.totalCellCount());
        }
        return connected;
    }

}
