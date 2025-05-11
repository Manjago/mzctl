package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.context.SimpleGameContext;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEngineTest {
    private static final String LOGIN = "tester";
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
        context = new SimpleGameContext(mazeManager);
        gameEngine = new GameEngineImpl(context);

        Cell position;
        Facing facing;
        do {
            gameEngine.loadMaze("test", LOGIN);
            position = context.getPlayerSession(LOGIN).getPlayerStateND().getPosition();
            facing = context.getPlayerSession(LOGIN).getPlayerStateND().getFacing();
        }while (!position.equals(Cell.ofRowAndColumn(0, 0)) || facing != Facing.NORTH);

        assertEquals(Cell.ofRowAndColumn(0, 0), position);
        assertEquals(Facing.NORTH, facing);

        final MazeEnvironmentDescriber describer = context.getPlayerSession(LOGIN).getMazeEnvironmentDescriber();
        final PlayerStateND playerState = context.getPlayerSession(LOGIN).getPlayerStateND();

        final Maze maze = context.getPlayerSession(LOGIN).getMaze();
        new MazeAsciiVisualizer(maze).printMaze(position, facing);
        System.out.println(describer.describeEnvironment(playerState));


    }
}