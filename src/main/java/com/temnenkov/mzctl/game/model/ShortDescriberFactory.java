package com.temnenkov.mzctl.game.model;

import com.temnenkov.mzctl.gameengine.EnvironmentDescriberFactory;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

public class ShortDescriberFactory implements EnvironmentDescriberFactory {
    @Override
    public @NotNull EnvironmentDescriber create(@NotNull Maze maze) {
        return new ShortEnvironmentDescriber(maze);
    }
}