package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MazeEnvironmentDescriber.class, name = "maze"),
        @JsonSubTypes.Type(value = ShortEnvironmentDescriber.class, name = "short")
})
public interface EnvironmentDescriber {
    @NotNull String describeEnvironment(@NotNull PlayerStateND player);
}