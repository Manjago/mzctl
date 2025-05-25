package com.temnenkov.mzctl.game.model;

import com.temnenkov.mzctl.auth.Role;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlayerSessionTest {
    @Test
    void testSaveAndLoadNullVersion(@TempDir @NotNull Path tempDir) {
        final String login  = "tester";
        final Maze maze = MazeFactory.createFullConnectedMaze(MazeDim.of(3, 3));
        final PlayerStateND playerState = new PlayerStateND(maze.getRandomCell(new Random(42)), Facing.SOUTH);

        final PlayerSession playerSession = createTestPlayerSession(login, maze, playerState, null);

        final Path file = tempDir.resolve("test.mzpack");
        SerializationHelper.savePlayerSessionToFile(playerSession, file.toString());

        final PlayerSession loadedPlayerSession = SerializationHelper.loadPlayerSessionFromFile(file.toString());
        assertNotNull(loadedPlayerSession);
        assertEquals(playerSession, loadedPlayerSession);
    }

    @Test
    void testSaveAndLoadNotNullVersion(@TempDir @NotNull Path tempDir) {
        final String login  = "tester";
        final Maze maze = MazeFactory.createFullConnectedMaze(MazeDim.of(3, 3));
        final PlayerStateND playerState = new PlayerStateND(maze.getRandomCell(new Random(42)), Facing.SOUTH);

        final PlayerSession playerSession = createTestPlayerSession(login, maze, playerState, 5L);

        final Path file = tempDir.resolve("test.mzpack");
        SerializationHelper.savePlayerSessionToFile(playerSession, file.toString());

        final PlayerSession loadedPlayerSession = SerializationHelper.loadPlayerSessionFromFile(file.toString());
        assertNotNull(loadedPlayerSession);
        assertEquals(playerSession, loadedPlayerSession);
    }

    private PlayerSession createTestPlayerSession(String login, Maze maze, PlayerStateND playerState, Long version) {
        return new PlayerSession(login, maze, new MazeEnvironmentDescriber(maze), playerState, Role.PLAYER, version);
    }
}