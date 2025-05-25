package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.context.SimpleGameContext;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.EnvironmentDescriber;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.game.model.RussianDescriberFactory;
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
        final PlayerPositionProvider fixedPositionProvider = new FixedPlayerPositionProvider(0, 0, Facing.NORTH);
        gameEngine = new GameEngineImpl(context, fixedPositionProvider, new RussianDescriberFactory());
    }

    @Test
    void testWalkThroughPredefinedMaze() throws IOException {
        final MazeManager mazeManager = new MazeManager(Path.of("src/test/resources"));
        context = new SimpleGameContext(mazeManager);
        final FixedPlayerPositionProvider fixedPlayerPositionProvider = new FixedPlayerPositionProvider(0, 0, Facing.NORTH);
        gameEngine = new GameEngineImpl(context, fixedPlayerPositionProvider, new RussianDescriberFactory());
        gameEngine.loadMaze("test", LOGIN);

        final Cell position = context.getPlayerSession(LOGIN).getPlayerStateND().getPosition();
        final Facing facing = context.getPlayerSession(LOGIN).getPlayerStateND().getFacing();

        assertEquals(Cell.ofRowAndColumn(0, 0), position, "Игрок должен стартовать в позиции (0,0)");
        assertEquals(Facing.NORTH, facing, "Игрок должен смотреть на север при старте");

        final EnvironmentDescriber describer = context.getPlayerSession(LOGIN).getMazeEnvironmentDescriber();
        final PlayerStateND playerState = context.getPlayerSession(LOGIN).getPlayerStateND();

        final Maze maze = context.getPlayerSession(LOGIN).getMaze();
        new MazeAsciiVisualizer(maze).printMaze(position, facing);
        System.out.println(describer.describeEnvironment(playerState));


    }
}