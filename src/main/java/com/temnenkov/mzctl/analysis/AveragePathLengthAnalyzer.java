package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class AveragePathLengthAnalyzer {
    private final Maze maze;

    public AveragePathLengthAnalyzer(Maze maze) {
        this.maze = maze;
    }

    public double averagePathLength() {
        final int cellCount = maze.totalCellCount();

        if (cellCount <= 1) {
            return 0.0; // вырожденный случай, нет путей между комнатами
        }

        long totalDistance = 0;

        // Запускаем BFS из каждой комнаты
        for (Cell cell : maze) {
            totalDistance += bfsTotalDistance(cell);
        }

        // Количество пар комнат = N * (N - 1)
        return (double) totalDistance / (cellCount * (cellCount - 1));
    }

    // BFS из одной комнаты, сумма расстояний до всех остальных
    private long bfsTotalDistance(@NotNull Cell start) {
        final Queue<Cell> queue = new ArrayDeque<>();
        final Map<Cell, Integer> distances = new HashMap<>();
        queue.add(start);
        distances.put(start, 0);

        long total = 0;

        while (!queue.isEmpty()) {
            final Cell current = queue.poll();
            int currentDist = distances.get(current);
            total += currentDist;

            for (Cell neighbor : maze.getAvailableNeighbors(current)) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, currentDist + 1);
                    queue.add(neighbor);
                }
            }
        }
        return total;
    }
}
