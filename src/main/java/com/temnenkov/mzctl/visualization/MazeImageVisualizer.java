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

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelX = x * (cellSize + wallSize) + wallSize;
                int pixelY = y * (cellSize + wallSize) + wallSize;

                Cell current = Cell.of(y, x);

                // верхняя стена (рисуем всегда, если верхняя граница)
                if (y == 0 || !maze.canPass(current, Cell.of(y - 1, x))) {
                    g.fillRect(pixelX - wallSize, pixelY - wallSize, cellSize + wallSize, wallSize);
                }

                // левая стена (рисуем всегда, если левая граница)
                if (x == 0 || !maze.canPass(current, Cell.of(y, x - 1))) {
                    g.fillRect(pixelX - wallSize, pixelY - wallSize, wallSize, cellSize + wallSize);
                }

                // правая стена (рисуем всегда, если правая граница)
                if (x == width - 1 || !maze.canPass(current, Cell.of(y, x + 1))) {
                    g.fillRect(pixelX + cellSize, pixelY - wallSize, wallSize, cellSize + wallSize);
                }

                // нижняя стена (рисуем всегда, если нижняя граница)
                if (y == height - 1 || !maze.canPass(current, Cell.of(y + 1, x))) {
                    g.fillRect(pixelX - wallSize, pixelY + cellSize, cellSize + wallSize, wallSize);
                }
            }
        }

        g.dispose();
        ImageIO.write(image, "png", new File(filename));
    }
}