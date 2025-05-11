package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

public class PlayerSession {
    private final String login;
    private final Maze maze;
    private final MazeEnvironmentDescriber mazeEnvironmentDescriber;
    private final PlayerStateND playerStateND;

    @JsonCreator
    public PlayerSession(@JsonProperty("login") @NotNull String login,
            @JsonProperty("maze") @NotNull Maze maze,
            @JsonProperty("mazeEnvironmentDescriber") @NotNull MazeEnvironmentDescriber mazeEnvironmentDescriber,
            @JsonProperty("playerStateND") @NotNull PlayerStateND playerStateND) {
        this.login = login;
        this.maze = maze;
        this.mazeEnvironmentDescriber = mazeEnvironmentDescriber;
        this.playerStateND = playerStateND;
    }

    public String getLogin() {
        return login;
    }

    public Maze getMaze() {
        return maze;
    }

    public MazeEnvironmentDescriber getMazeEnvironmentDescriber() {
        return mazeEnvironmentDescriber;
    }

    public PlayerStateND getPlayerStateND() {
        return playerStateND;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        PlayerSession that = (PlayerSession) o;
        return login.equals(that.login) && maze.equals(that.maze) && mazeEnvironmentDescriber.equals(that.mazeEnvironmentDescriber) && playerStateND.equals(that.playerStateND);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + maze.hashCode();
        result = 31 * result + mazeEnvironmentDescriber.hashCode();
        result = 31 * result + playerStateND.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PlayerSession{" + "login='" + login + '\'' + ", maze=" + maze + ", mazeEnvironmentDescriber=" + mazeEnvironmentDescriber + ", playerStateND=" + playerStateND + '}';
    }
}
