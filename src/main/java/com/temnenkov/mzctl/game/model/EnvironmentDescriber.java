package com.temnenkov.mzctl.game.model;

import org.jetbrains.annotations.NotNull;

public interface EnvironmentDescriber {
    @NotNull String describeEnvironment(@NotNull PlayerStateND player);
}