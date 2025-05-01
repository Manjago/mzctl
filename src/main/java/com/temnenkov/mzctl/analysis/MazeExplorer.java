package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

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
        return new ConnectednessAnalyzer(maze, random).isConnected();
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
        return new AcyclicityAnalyzer(maze).isAcyclic();
    }

    /**
     * Проверка, что лабиринт perfect (connected + acyclic)
     *
     * @return true, если лабиринт perfect, false в противном случае
     */
    public boolean isPerfect() {
        return isConnected() && isAcyclic();
    }

    public long deadEndCount() {
        return new DeadEndAnalyzer(maze).deadEndCount();
    }

    public int diameter() {
        return new DiameterAnalyzer(maze, random).diameter();
    }

    public double averagePathLength() {
        return new AveragePathLengthAnalyzer(maze).averagePathLength();
    }

    public long intersectionCount() {
        return new IntersectionAnalyzer(maze).intersectionCount();
    }

    public double randomnessScore() {
        return new RandomnessAnalyzer(maze).randomnessScore();
    }

    public double balanceScore() {
        return new BalanceAnalyzer(maze).balanceScore();
    }

    public double symmetryScore() {
        return new SymmetryAnalyzer(maze).symmetryScore();
    }

    /**
     * Возвращает текстовый отчет с основными характеристиками лабиринта.
     *
     * @return строка отчета
     */
    public String report() {
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
        Symmetry Score: %.2f
        """.formatted(
                    isConnected(),
                    isAcyclic(),
                    isPerfect(),
                    deadEndCount(),
                    diameter(),
                    averagePathLength(),
                    intersectionCount(),
                    randomnessScore(),
                    symmetryScore()
            );
        }
}

