package com.temnenkov.mzctl.visualization;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.model.MazeDim;

public class CellularAutomataAsciiVisualizer {

    private final CellularAutomataMaze maze;

    public CellularAutomataAsciiVisualizer(CellularAutomataMaze maze) {
        this.maze = maze;
    }

    public void print() {
        MazeDim dim = maze.getDimensions();
        if (dim.size() != 2) {
            throw new UnsupportedOperationException("Only 2D mazes supported for ASCII visualization");
        }

        int width = dim.dimSize(1);
        int height = dim.dimSize(0);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze.isWall(Cell.of(y, x))) {
                    System.out.print("#"); // стена
                } else {
                    System.out.print("."); // пустое место
                }
            }
            System.out.println();
        }
    }
}