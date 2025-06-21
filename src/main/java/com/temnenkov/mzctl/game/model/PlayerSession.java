package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.auth.Role;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.UserId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PlayerSession {
    private final String login;
    private final Maze maze;
    private final EnvironmentDescriber mazeEnvironmentDescriber;
    private final PlayerStateND playerStateND;
    private final Role role;
    private Long version;

    @JsonCreator
    public PlayerSession(@JsonProperty("login") @NotNull String login,
            @JsonProperty("maze") @NotNull Maze maze,
            @JsonProperty("mazeEnvironmentDescriber") @NotNull EnvironmentDescriber environmentDescriber,
            @JsonProperty("playerStateND") @NotNull PlayerStateND playerStateND,
            @JsonProperty("role") @NotNull Role role,
            @JsonProperty("version") @Nullable Long version) {
        this.login = login;
        this.maze = maze;
        this.mazeEnvironmentDescriber = environmentDescriber;
        this.playerStateND = playerStateND;
        this.role = role;
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getLogin() {
        return login;
    }

    public Maze getMaze() {
        return maze;
    }

    public EnvironmentDescriber getMazeEnvironmentDescriber() {
        return mazeEnvironmentDescriber;
    }

    public PlayerStateND getPlayerStateND() {
        return playerStateND;
    }

    public Role getRole() {
        return role;
    }

    public UserId getUserId() {
        return new UserId(login);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        PlayerSession that = (PlayerSession) o;
        return login.equals(that.login) && maze.equals(that.maze) && mazeEnvironmentDescriber.equals(that.mazeEnvironmentDescriber) && playerStateND.equals(that.playerStateND) && role == that.role && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + maze.hashCode();
        result = 31 * result + mazeEnvironmentDescriber.hashCode();
        result = 31 * result + playerStateND.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public String toString() {
        return "PlayerSession{" + "login='" + login + '\'' + ", maze=" + maze + ", mazeEnvironmentDescriber=" + mazeEnvironmentDescriber + ", playerStateND=" + playerStateND + ", role=" + role + ", version=" + version + '}';
    }
}
