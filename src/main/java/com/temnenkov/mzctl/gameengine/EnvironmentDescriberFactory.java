package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.game.model.EnvironmentDescriber;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

public interface EnvironmentDescriberFactory {
    @NotNull EnvironmentDescriber create(@NotNull Maze maze);
}