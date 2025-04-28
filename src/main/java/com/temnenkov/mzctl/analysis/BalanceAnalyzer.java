package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Maze;

public class BalanceAnalyzer {
    private final Maze maze;

    public BalanceAnalyzer(Maze maze) {
        this.maze = maze;
    }

    public double balanceScore() {
        long deadEnds = new DeadEndAnalyzer(maze).deadEndCount();
        long intersections = new IntersectionAnalyzer(maze).intersectionCount();

        if (deadEnds + intersections == 0) {
            return 1.0; // крайний случай, нет тупиков и перекрёстков, считаем идеально сбалансированным
        }

        return 1.0 - ((double) Math.abs(deadEnds - intersections) / (deadEnds + intersections));
    }
}