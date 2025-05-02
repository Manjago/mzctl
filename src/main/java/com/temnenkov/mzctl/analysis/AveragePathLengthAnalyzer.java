package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;

import java.util.List;
import java.util.Random;

public class AveragePathLengthAnalyzer {

    private static final int SAMPLE_SIZE = 1000; // размер случайной выборки пар ячеек
    private static final int FULL_ENUMERATION_THRESHOLD = 10_000; // порог количества пар

    private final Maze maze;
    private final Random random;

    public AveragePathLengthAnalyzer(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
    }

    public double averagePathLength() {
        int totalCells = maze.totalCellCount();
        long totalPairs = ((long) totalCells * (totalCells - 1)) / 2;

        if (totalPairs < FULL_ENUMERATION_THRESHOLD) {
            // Полный перебор
            return averagePathLengthFullEnumeration();
        } else {
            // Случайная выборка
            return averagePathLengthRandomSample(SAMPLE_SIZE);
        }
    }

    private double averagePathLengthFullEnumeration() {
        List<Cell> cells = maze.stream().toList();
        long totalLength = 0;
        long pairsCounted = 0;

        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                int pathLength = ShortestPathHelper.shortestDistance(maze, cells.get(i), cells.get(j));
                totalLength += pathLength;
                pairsCounted++;
            }
        }
        return pairsCounted > 0 ? (double) totalLength / pairsCounted : 0.0;
    }

    private double averagePathLengthRandomSample(int sampleSize) {
        List<Cell> cells = maze.stream().toList();
        long totalLength = 0;

        for (int i = 0; i < sampleSize; i++) {
            Cell start = cells.get(random.nextInt(cells.size()));
            Cell end = cells.get(random.nextInt(cells.size()));
            if (start.equals(end)) {
                i--;
                continue; // пропускаем повторные ячейки
            }
            totalLength += ShortestPathHelper.shortestDistance(maze, start, end);
        }
        return sampleSize > 0 ? (double) totalLength / sampleSize : 0.0;
    }

}