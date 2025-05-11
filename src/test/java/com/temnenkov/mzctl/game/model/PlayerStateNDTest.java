package com.temnenkov.mzctl.game.model;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerStateNDTest {
    @Test
    void testSaveAndLoad(@TempDir @NotNull Path tempDir) {
        final PlayerStateND playerState = new PlayerStateND(Cell.of(1, 2), Facing.SOUTH);
        final Path file = tempDir.resolve("playerState.mzpack");
        SerializationHelper.savePlayerStateToFile(playerState, file.toString());
        final PlayerStateND loadedPlayerState = SerializationHelper.loadPlayerStateFromFile(file.toString());
        assertEquals(playerState, loadedPlayerState);
    }

}