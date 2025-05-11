package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.context.SimpleGameContext;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameEngineTest {
    private GameEngine gameEngine;
    private GameContext context;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        MazeManager mazeManager = new MazeManager(tempDir);
        context = new SimpleGameContext(mazeManager);
        gameEngine = new GameEngineImpl(context);
    }

    @Test
    void testWalkThroughPredefinedMaze() throws IOException {
        final MazeManager mazeManager = new MazeManager(Path.of("src/test/resources"));
        final Maze maze = mazeManager.loadMaze("test");
        assertNotNull(maze);
        new MazeAsciiVisualizer(maze).printMaze(Cell.ofRowAndColumn(1, 0), Facing.NORTH);
        new MazeAsciiVisualizer(maze).printMaze(Cell.ofRowAndColumn(0, 1), Facing.SOUTH);

    }
}