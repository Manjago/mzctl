package com.temnenkov.mzctl.visualization;

import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.model.MazeDim;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CellularAutomataImageVisualizer {

    private final CellularAutomataMaze maze;
    private final int cellSize;

    public CellularAutomataImageVisualizer(CellularAutomataMaze maze, int cellSize) {
        this.maze = maze;
        this.cellSize = cellSize;
    }

    public void saveImage(String filename) throws IOException {
        MazeDim dim = maze.getDimensions();
        if (dim.size() != 2) {
            throw new UnsupportedOperationException("Only 2D mazes supported for PNG visualization");
        }

        int width = dim.dimSize(1);
        int height = dim.dimSize(0);

        int imageWidth = (width + 2) * cellSize;
        int imageHeight = (height + 2) * cellSize;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Рисуем фон (белый цвет)
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imageWidth, imageHeight);

        // Рисуем внешнюю границу (рамку) серым цветом
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, imageWidth, cellSize); // верхняя граница
        g.fillRect(0, imageHeight - cellSize, imageWidth, cellSize); // нижняя граница
        g.fillRect(0, 0, cellSize, imageHeight); // левая граница
        g.fillRect(imageWidth - cellSize, 0, cellSize, imageHeight); // правая граница

        // Рисуем стены лабиринта (черный цвет)
        g.setColor(Color.BLACK);
        maze.stream().filter(maze::isWall).forEach(cell -> {
            int y = cell.coord(0);
            int x = cell.coord(1);
            g.fillRect((x + 1) * cellSize, (y + 1) * cellSize, cellSize, cellSize);
        });

        g.dispose();
        ImageIO.write(image, "png", new File(filename));
    }
}