package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.CellularAutomataMaze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.visualization.CellularAutomataAsciiVisualizer;
import com.temnenkov.mzctl.visualization.CellularAutomataImageVisualizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CellularAutomataMazeGeneratorTest {

    @Test
    void generate() throws IOException {
        final CellularAutomataMazeGenerator mazeGenerator = new CellularAutomataMazeGenerator(0.75, 10,
                new Random(42L));
        final CellularAutomataMaze maze = mazeGenerator.generate(MazeDim.of(10, 10));
        assertNotNull(maze);
        new CellularAutomataAsciiVisualizer(maze).print();
        new CellularAutomataImageVisualizer(maze, 10).saveImage("target/cellular_maze.png");
    }
}