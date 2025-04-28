package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Maze;

public class DeadEndAnalyzer {
    private final Maze maze;

    public DeadEndAnalyzer(Maze maze) {
        this.maze = maze;
    }

    public long deadEndCount() {
        return maze.stream()
                .filter(c -> maze.getAvailableNeighbors(c).size() == 1)
                .count();
    }

}
