package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AveragePathLengthAnalyzer {

    /**
     * Размер выборки для случайного анализа.
     * Значение выбрано эмпирически, обеспечивает приемлемую точность.
     */
    private static final int SAMPLE_SIZE = 1000;

    /**
     * Максимальное количество пар, при котором используется полный перебор.
     * Значение выбрано исходя из соображений производительности.
     */
    private static final int FULL_ENUMERATION_THRESHOLD = 10_000;

    private final Maze maze;
    private final Random random;

    public AveragePathLengthAnalyzer(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
    }

    public double averagePathLength() {
        final int totalCells = maze.totalCellCount();
        final long totalPairs = ((long) totalCells * (totalCells - 1)) / 2;

        if (totalPairs < FULL_ENUMERATION_THRESHOLD) {
            // Полный перебор для небольших лабиринтов
            return averagePathLengthFullEnumeration();
        } else {
            // Случайная выборка для больших лабиринтов
            return averagePathLengthRandomSample();
        }
    }

    private double averagePathLengthFullEnumeration() {
        List<Cell> cells = maze.stream().toList();

        if (cells.size() < 2) {
            // Вырожденный случай: нет пар ячеек
            return 0.0;
        }

        long totalLength = 0;
        long pairsCounted = 0;

        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                int pathLength = ShortestPathHelper.shortestDistance(maze, cells.get(i), cells.get(j));
                if (pathLength < 0) {
                    throw new IllegalStateException("Maze is not fully connected!");
                }
                totalLength += pathLength;
                pairsCounted++;
            }
        }

        return (double) totalLength / pairsCounted;
    }

    private double averagePathLengthRandomSample() {
        final List<Cell> cells = maze.stream().toList();

        if (cells.size() < 2) {
            // Вырожденный случай: нет пар ячеек
            return 0.0;
        }

        // Максимальное возможное количество уникальных пар
        final long maxPossiblePairs = ((long) cells.size() * (cells.size() - 1)) / 2;
        final int actualSampleSize = (int) Math.min(SAMPLE_SIZE, maxPossiblePairs);

        long totalLength = 0;
        int validSamples = 0;
        final Set<String> uniquePairs = new HashSet<>();

        while (validSamples < actualSampleSize) {
            Cell start = cells.get(random.nextInt(cells.size()));
            Cell end = cells.get(random.nextInt(cells.size()));

            if (start.equals(end)) {
                continue; // Пропускаем одинаковые ячейки
            }

            final String pairKey = generatePairKey(start, end);
            if (uniquePairs.contains(pairKey)) {
                continue; // Пропускаем уже рассмотренные пары
            }

            uniquePairs.add(pairKey);

            final int pathLength = ShortestPathHelper.shortestDistance(maze, start, end);
            if (pathLength < 0) {
                throw new IllegalStateException("Maze is not fully connected!");
            }

            totalLength += pathLength;
            validSamples++;
        }

        return (double) totalLength / validSamples;
    }

    /**
     * Генерирует уникальный ключ для пары ячеек, порядок не имеет значения.
     */
    private @NotNull String generatePairKey(@NotNull Cell a, @NotNull Cell b) {
        return a.hashCode() < b.hashCode()
                ? a + "-" + b
                : b + "-" + a;
    }
}