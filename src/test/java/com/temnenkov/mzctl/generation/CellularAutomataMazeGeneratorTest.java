package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.visualization.CellularAutomataAsciiVisualizer;
import com.temnenkov.mzctl.visualization.CellularAutomataImageVisualizer;
import com.temnenkov.mzctl.visualization.MazeVisualizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CellularAutomataMazeGeneratorTest {

    @Test
    void generateWithAsciiVisualization() {
        MazeVisualizer visualizer = (maze, step) -> {
            System.out.printf("Step #%d:%n", step);
            new CellularAutomataAsciiVisualizer(maze).print();
            try {
                new CellularAutomataImageVisualizer(maze, 5).saveImage("target/step" + step + ".png");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println();
        };

        final CellularAutomataMazeGenerator mazeGenerator = new CellularAutomataMazeGenerator(
               0.75, 10, 0.5, 0.625, new Random(42L), visualizer);
        final CellularAutomataMaze maze = mazeGenerator.generate(MazeDim.of(10, 10));
        assertNotNull(maze);
    }
}