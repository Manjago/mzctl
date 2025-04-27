package com.temnenkov.mzctl.util;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

public class MazeAsciiVisualizer {

    private final Maze maze;
    private final int width;
    private final int height;

    public MazeAsciiVisualizer(@NotNull Maze maze) {
        if (maze.getMazeDimension().size() != 2) {
            throw new IllegalArgumentException("Only 2-dimensional mazes supported");
        }
        this.maze = maze;
        this.height = maze.getMazeDimension().dimSize(0);
        this.width = maze.getMazeDimension().dimSize(1);
    }

    public void printMaze() {
        // верхняя граница лабиринта
        System.out.print("+");
        for (int x = 0; x < width; x++) {
            System.out.print("---+");
        }
        System.out.println();

        for (int y = 0; y < height; y++) {
            // левая стена первого столбца
            System.out.print("|");
            // горизонтальный проход между комнатами
            for (int x = 0; x < width; x++) {
                Cell current = Cell.of(y, x);
                // если проход вправо есть, не рисуем стену справа
                if (x < width - 1 && maze.canPass(current, Cell.of(y, x + 1))) {
                    System.out.print("    ");
                } else {
                    System.out.print("   |");
                }
            }
            System.out.println();

            // стены под текущим рядом комнат
            System.out.print("+");
            for (int x = 0; x < width; x++) {
                Cell current = Cell.of(y, x);
                // если проход вниз есть, не рисуем стену снизу
                if (y < height - 1 && maze.canPass(current, Cell.of(y + 1, x))) {
                    System.out.print("   +");
                } else {
                    System.out.print("---+");
                }
            }
            System.out.println();
        }
    }
}