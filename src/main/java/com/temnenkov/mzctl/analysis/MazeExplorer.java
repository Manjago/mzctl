package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.util.SimpleStopWatch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class MazeExplorer {
    private static final Logger logger = LoggerFactory.getLogger(MazeExplorer.class);

    private static final int DEFAULT_SAMPLE_SIZE = 1000;

    private final Maze maze;
    private final Random random;
    private Boolean isConnectedCache = null;
    private Boolean isAcyclicCache = null;

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
        if (isConnectedCache == null) {
            final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
            isConnectedCache = new ConnectednessAnalyzer(maze, random).isConnected();
            final long elapsedMs = stopWatch.elapsed();
            logger.trace("Connectedness check elapsed time: {} ms", elapsedMs);

        }
        return isConnectedCache;
    }

    /**
     * Проверка, что лабиринт connected
     *
     * @param startCell комната, с которой начинаем
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected(@NotNull Cell startCell) {
        return new ConnectednessAnalyzer(maze, random).isConnected(startCell);
    }

    /**
     * Проверка, что лабиринт не содержит циклов
     *
     * @return true, если лабиринт не содержит циклов, false в противном случае
     */
    public boolean isAcyclic() {
        if (isAcyclicCache == null) {
            final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
            isAcyclicCache = new AcyclicityAnalyzer(maze).isAcyclic();
            final long elapsedMs = stopWatch.elapsed();
            logger.trace("Acyclic check elapsed time: {} ms", elapsedMs);
        }
        return isAcyclicCache;
    }

    /**
     * Проверка, что лабиринт perfect (connected + acyclic)
     *
     * @return true, если лабиринт perfect, false в противном случае
     */
    public boolean isPerfect() {
        return isPerfect(isConnected(), isAcyclic());
    }

    /**
     * Проверка, что лабиринт perfect (connected + acyclic).
     * Использует уже вычисленные значения connected и acyclic.
     *
     * @param isConnected уже вычисленное значение connected
     * @param isAcyclic уже вычисленное значение acyclic
     * @return true, если лабиринт perfect, false в противном случае
     */
    public boolean isPerfect(boolean isConnected, boolean isAcyclic) {
        return isConnected && isAcyclic;
    }

    public long deadEndCount() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        long result = new DeadEndAnalyzer(maze).deadEndCount();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("DeadEnd check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public int diameter() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final int result = new DiameterAnalyzer(maze, random).diameter();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("Diameter check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public double averagePathLength(int sampleSize) {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final double result = new AveragePathLengthAnalyzer(maze, random, sampleSize).averagePathLength();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("AveragePathLength check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public double averagePathLength() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final double result = new AveragePathLengthAnalyzer(maze, random).averagePathLength();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("AveragePathLength check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public long intersectionCount() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final long result = new IntersectionAnalyzer(maze).intersectionCount();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("IntersectionCount check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public double randomnessScore() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final double result = new RandomnessAnalyzer(maze).randomnessScore();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("RandomnessScore check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public double balanceScore() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final double result = new BalanceAnalyzer(maze).balanceScore();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("BalanceScore check elapsed time: {} ms", elapsedMs);
        return result;
    }

    public double symmetryScore() {
        final SimpleStopWatch stopWatch = SimpleStopWatch.createStarted();
        final double result = new SymmetryAnalyzer(maze).symmetryScore();
        final long elapsedMs = stopWatch.elapsed();
        logger.trace("SymmetryScore check elapsed time: {} ms", elapsedMs);
        return result;
    }

    /**
     * Возвращает текстовый отчет с основными характеристиками лабиринта.
     *
     * @return строка отчета
     */
    public String report() {
        return report(DEFAULT_SAMPLE_SIZE);
    }

    /**
     * Возвращает текстовый отчет с основными характеристиками лабиринта.
     * Позволяет явно указать количество выборок для анализа средней длины пути.
     *
     * @param sampleSize количество выборок для анализа средней длины пути
     * @return строка отчета
     */
    public String report(int sampleSize) {
        final boolean acyclic = isAcyclic();
        final boolean connected = isConnected();
        return """
    Maze exploration:
    Connected: %s
    Acyclic: %s
    Perfect: %s
    Dead End Count: %d
    Diameter: %d
    Average Path Length: %.2f
    Intersection Count: %d
    Randomness Score: %.2f
    Symmetry Score: %s
    """.formatted(connected,
                acyclic,
                isPerfect(connected, acyclic),
                deadEndCount(),
                diameter(),
                averagePathLength(sampleSize),
                intersectionCount(),
                randomnessScore(),
                maze.getMazeDimension().size() == 2 ? String.format("%.2f", symmetryScore()) : "N/A"
        );
    }
}

