package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.analysis.MazeExplorer;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeManagerTest {

    private MazeManager mazeManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        mazeManager = new MazeManager(tempDir);
    }

    @Test
    void testGenerateMaze2D() {
        Maze maze = mazeManager.generateMaze2D(10, 10, MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);
        assertNotNull(maze);
        assertEquals(MazeDim.of(10, 10), maze.getMazeDimension());
        assertTrue(new MazeExplorer(maze, new SecureRandom()).isPerfect());
    }

    @Test
    void testSaveAndLoadMaze() {
        Maze originalMaze = mazeManager.generateMaze2D( 5, 5, MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);
        mazeManager.saveMaze("testMaze", originalMaze);

        Maze loadedMaze = mazeManager.loadMaze("testMaze");
        assertEquals(originalMaze, loadedMaze);
    }

}