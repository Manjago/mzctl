package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

public class SymmetryAnalyzer {
    private final Maze maze;
    private final int height;
    private final int width;

    public SymmetryAnalyzer(@NotNull Maze maze) {
        if (maze.getMazeDimension().size() != 2) {
            throw new IllegalArgumentException("Only 2-dimensional mazes supported");
        }
        this.maze = maze;
        this.height = maze.getMazeDimension().dimSize(0);
        this.width = maze.getMazeDimension().dimSize(1);
    }

    public double symmetryScore() {
        int totalChecks = 0;
        int symmetricMatches = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Cell current = Cell.of(y, x);
                final Cell mirror = Cell.of(y, width - x - 1); // отражение по вертикали

                for (Cell neighbor : maze.getAvailableNeighbors(current)) {
                    // находим симметричного соседа
                    final int neighborY = neighbor.coord(0);
                    final int neighborX = neighbor.coord(1);
                    final Cell neighborMirror = Cell.of(neighborY, width - neighborX - 1);

                    totalChecks++;

                    // проверяем, есть ли аналогичный проход в симметричном месте
                    if (maze.getAvailableNeighbors(mirror).contains(neighborMirror)) {
                        symmetricMatches++;
                    }
                }
            }
        }

        if (totalChecks == 0) {
            // крайний случай, пустой лабиринт без проходов
            return 1.0;
        }

        // доля симметричных проходов
        return (double) symmetricMatches / totalChecks;
    }
}