package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Maze;

/**
 * Анализатор количества перекрёстков в лабиринте.
 * Перекрёсток — это комната, у которой три или более проходов.
 */
public class IntersectionAnalyzer {
    private final Maze maze;

    public IntersectionAnalyzer(Maze maze) {
        this.maze = maze;
    }

    /**
     * Возвращает количество перекрёстков.
     * @return количество комнат с тремя или более проходами
     */
    public long intersectionCount() {
        return (int) maze.stream()
                .filter(c -> maze.getAvailableNeighbors(c).size() >= 3)
                .count();
    }
}
