package com.temnenkov.mzctl.context;

import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleGameContext implements GameContext {
    private final MazeManager mazeManager;

    public SimpleGameContext(MazeManager mazeManager) {
        this.mazeManager = mazeManager;
    }

    @Override
    public MazeManager getMazeManager() {
        return mazeManager;
    }

    private final ConcurrentMap<String, PlayerSession> playerSessions = new ConcurrentHashMap<>();

    @Override
    public PlayerSession getPlayerSession(String login) {
        return playerSessions.get(login);
    }

    @Override
    public void createPlayerSession(@NotNull PlayerSession playerSession) {
        playerSessions.put(playerSession.getLogin(), playerSession);
    }

    @Override
    public void updatePlayerSession(@NotNull PlayerSession playerSession) {
        final Long version = playerSession.getVersion();
        final PlayerSession oldPlayerSession = playerSessions.get(playerSession.getLogin());
        SimplePreconditions.checkState(oldPlayerSession != null, "Player session not found");
        SimplePreconditions.checkState(Objects.equals(oldPlayerSession.getVersion(), version), "Player session versions do not match");
        playerSession.setVersion(version == null ? 0L : version + 1);
        playerSessions.put(playerSession.getLogin(), playerSession);
    }

}
