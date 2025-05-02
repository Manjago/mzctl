package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.util.SimpleStopWatch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AveragePathLengthAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(AveragePathLengthAnalyzer.class);

    /**
     * Размер выборки для случайного анализа по умолчанию.
     */
    private static final int DEFAULT_SAMPLE_SIZE = 1000;

    /**
     * Максимальное количество пар, при котором используется полный перебор.
     * Значение выбрано исходя из соображений производительности.
     */
    private static final int FULL_ENUMERATION_THRESHOLD = 10_000;

    private final Maze maze;
    private final Random random;
    private final int sampleSize;

    public AveragePathLengthAnalyzer(Maze maze, Random random) {
        this(maze, random, DEFAULT_SAMPLE_SIZE);
    }

    public AveragePathLengthAnalyzer(Maze maze, Random random, int sampleSize) {
        this.maze = maze;
        this.random = random;
        this.sampleSize = sampleSize;
    }

    public double averagePathLength() {
        final int totalCells = maze.totalCellCount();
        final long totalPairs = ((long) totalCells * (totalCells - 1)) / 2;

        final List<Cell> allMazeCells = maze.stream().toList();
        if (allMazeCells.size() < 2) {
            log.trace("Degenerate case: less than two cells, returning 0.0");
            return 0.0;
        }

        if (totalPairs < FULL_ENUMERATION_THRESHOLD) {
            log.trace("Performing full enumeration analysis. Total pairs: {}", totalPairs);
            final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
            double result = averagePathLengthFullEnumeration(allMazeCells);
            final long elapsedMs = stopWatch.elapsed();
            log.trace("Full enumeration completed in {} ms", elapsedMs);
            return result;
        } else {
            log.trace("Performing random sample analysis. Max possible pairs: {}, Sample size: {}", totalPairs, sampleSize);
            final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
            double result = averagePathLengthRandomSample(allMazeCells);
            final long elapsedMs = stopWatch.elapsed();
            log.trace("Random sampling completed in {} ms", elapsedMs);
            return result;
        }
    }

    private double averagePathLengthFullEnumeration(@NotNull List<Cell> cells) {
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

        log.trace("Full enumeration analyzed {} pairs", pairsCounted);
        if (pairsCounted == 0) {
            // Это состояние невозможно при текущей логике. Если возникло, значит ошибка в коде.
            throw new IllegalStateException("Unexpected state: pairsCounted is zero");
        }
        return (double) totalLength / pairsCounted;
    }

    private double averagePathLengthRandomSample(@NotNull List<Cell> cells) {
        final long maxPossiblePairs = ((long) cells.size() * (cells.size() - 1)) / 2;
        final int actualSampleSize = (int) Math.min(sampleSize, maxPossiblePairs);

        long totalLength = 0;
        int validSamples = 0;
        final Set<CellPair> uniquePairs = new HashSet<>();

        while (validSamples < actualSampleSize) {
            final Cell start = cells.get(random.nextInt(cells.size()));
            final Cell end = cells.get(random.nextInt(cells.size()));

            if (start.equals(end)) {
                continue;
            }

            final CellPair pair = new CellPair(start, end);
            if (uniquePairs.contains(pair)) {
                continue;
            }
            uniquePairs.add(pair);

            final int pathLength = ShortestPathHelper.shortestDistance(maze, start, end);
            if (pathLength < 0) {
                throw new IllegalStateException("Maze is not fully connected!");
            }

            totalLength += pathLength;
            validSamples++;
        }

        if (validSamples == 0) {
            throw new IllegalStateException("Unexpected state: validSamples is zero after sampling.");
        }

        return (double) totalLength / validSamples;
    }
    private record CellPair(Cell first, Cell second) {
        public CellPair {
            if (first.hashCode() > second.hashCode()) {
                Cell tmp = first;
                first = second;
                second = tmp;
            }
        }
    }
}