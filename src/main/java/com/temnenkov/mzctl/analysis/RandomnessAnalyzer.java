package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Maze;

public class RandomnessAnalyzer {
    private final Maze maze;

    public RandomnessAnalyzer(Maze maze) {
        this.maze = maze;
    }

    public double randomnessScore() {
        final long deadEnds = deadEndCount();
        final long intersections = intersectionCount();
        int totalCells = maze.totalCellCount();

        // простая формула случайности
        return (double) (deadEnds + intersections) / totalCells;
    }

    private long deadEndCount() {
        return (int) new DeadEndAnalyzer(maze).deadEndCount();
    }

    private long intersectionCount() {
        return (int) new IntersectionAnalyzer(maze).intersectionCount();
    }
}
