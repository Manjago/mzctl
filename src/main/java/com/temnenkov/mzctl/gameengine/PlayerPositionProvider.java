package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

public interface PlayerPositionProvider {
    @NotNull PlayerStateND createPlayerPosition(@NotNull Maze maze);
}
