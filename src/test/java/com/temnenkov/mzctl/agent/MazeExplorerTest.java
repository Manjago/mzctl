package com.temnenkov.mzctl.agent;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeExplorerTest {

    @Test
    void isConnected() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(1, 0), Cell.of(0, 1)));
        maze.addPass(Cell.of(0, 1), Set.of(Cell.of(1, 1)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isConnected(Cell.of(0, 0)));
        assertTrue(mazeExplorer.isConnected(Cell.of(1, 0)));
        assertTrue(mazeExplorer.isConnected(Cell.of(0, 1)));
        assertTrue(mazeExplorer.isConnected(Cell.of(1, 1)));
    }

    @Test
    void notConnected() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(1, 0), Cell.of(0, 1)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

        assertFalse(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isConnected(Cell.of(0, 0)));
        assertFalse(mazeExplorer.isConnected(Cell.of(1, 0)));
        assertFalse(mazeExplorer.isConnected(Cell.of(0, 1)));
        assertFalse(mazeExplorer.isConnected(Cell.of(1, 1)));
    }

    @Test
    void withoutPassesNotConnected() {
        final Maze maze = Maze.of(100, 100);
        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

        assertFalse(mazeExplorer.isConnected());
        for(int i = 0; i < 100; ++i) {
            for(int j = 0; j < 100; ++j) {
                assertFalse(mazeExplorer.isConnected(Cell.of(i, j)));
            }
        }
    }
}