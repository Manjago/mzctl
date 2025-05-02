package com.temnenkov.mzctl.visualization;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MazeImageVisualizer {

    private final Maze maze;
    private final int cellSize;
    private final int wallSize;
    private final int width;
    private final int height;

    public MazeImageVisualizer(@NotNull Maze maze, int cellSize, int wallSize) {
        if (maze.getMazeDimension().size() != 2) {
            throw new IllegalArgumentException("Only 2-dimensional mazes supported");
        }
        this.maze = maze;
        this.cellSize = cellSize;
        this.wallSize = wallSize;
        this.height = maze.getMazeDimension().dimSize(0);
        this.width = maze.getMazeDimension().dimSize(1);
    }

    public MazeImageVisualizer(Maze maze) {
        this(maze, 20, 10);
    }

    public void saveMazeImage(String filename) throws IOException {
        int imgWidth = width * cellSize + (width + 1) * wallSize;
        int imgHeight = height * cellSize + (height + 1) * wallSize;

        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // фон
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgWidth, imgHeight);

        // стены
        g.setColor(Color.BLACK);

        // Рисуем горизонтальные стены (включая верхнюю и нижнюю границы лабиринта)
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x < width; x++) {
                boolean drawWall;

                if (y == 0 || y == height) {
                    drawWall = true; // Верхняя и нижняя границы всегда рисуются
                } else {
                    Cell current = Cell.of(y - 1, x);
                    Cell below = Cell.of(y, x);
                    drawWall = !maze.canPass(current, below);
                }

                if (drawWall) {
                    int wallX = x * (cellSize + wallSize);
                    int wallY = y * (cellSize + wallSize);
                    g.fillRect(wallX, wallY, cellSize + 2 * wallSize, wallSize);
                }
            }
        }

        // Рисуем вертикальные стены (включая левую и правую границы лабиринта)
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y < height; y++) {
                boolean drawWall;

                if (x == 0 || x == width) {
                    drawWall = true; // Левая и правая границы всегда рисуются
                } else {
                    Cell current = Cell.of(y, x - 1);
                    Cell right = Cell.of(y, x);
                    drawWall = !maze.canPass(current, right);
                }

                if (drawWall) {
                    int wallX = x * (cellSize + wallSize);
                    int wallY = y * (cellSize + wallSize);
                    g.fillRect(wallX, wallY, wallSize, cellSize + 2 * wallSize);
                }
            }
        }

        g.dispose();
        ImageIO.write(image, "png", new File(filename));
    }
}