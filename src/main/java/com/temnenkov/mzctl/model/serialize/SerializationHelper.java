package com.temnenkov.mzctl.model.serialize;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class SerializationHelper {

    private SerializationHelper() {
    }

    public static void saveMazeToFile(@NotNull Maze maze, @NotNull String filename) {
        try {
            KryoHelper.saveToFile(maze, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save maze to file " + filename, e);
        }
    }

    public static @NotNull Maze loadMazeFromFile(@NotNull String filename) {
        try {
            return KryoHelper.loadFromFile(Maze.class, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read maze from file " + filename, e);
        }
    }

    public static void savePlayerSessionToFile(@NotNull PlayerSession playerSession, @NotNull String filename) {
        try {
            KryoHelper.saveToFile(playerSession, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save playerSession to file " + filename, e);
        }
    }

    public static @NotNull PlayerSession loadPlayerSessionFromFile(@NotNull String filename) {
        try {
            return KryoHelper.loadFromFile(PlayerSession.class, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read playerSession from file " + filename, e);
        }
    }

    public static void saveFacingToFile(@NotNull Facing facing, @NotNull String filename) {
        try {
            KryoHelper.saveToFile(facing, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save facing to file " + filename, e);
        }
    }

    public static @NotNull Facing loadFacingFromFile(@NotNull String filename) {
        try {
            return KryoHelper.loadFromFile(Facing.class, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read facing from file " + filename, e);
        }
    }

    public static void savePlayerStateToFile(@NotNull PlayerStateND playerState, @NotNull String filename) {
        try {
            KryoHelper.saveToFile(playerState, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save playerState to file " + filename, e);
        }
    }

    public static @NotNull PlayerStateND loadPlayerStateFromFile(@NotNull String filename) {
        try {
            return KryoHelper.loadFromFile(PlayerStateND.class, filename);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read playerState from file " + filename, e);
        }
    }

}