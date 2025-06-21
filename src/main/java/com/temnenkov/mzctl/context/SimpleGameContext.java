package com.temnenkov.mzctl.context;

import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.model.UserId;
import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleGameContext implements GameContext {
    private static final Logger logger = LoggerFactory.getLogger(SimpleGameContext.class);
    private final MazeManager mazeManager;
    private final AtomicReference<UserId> currentUserId = new AtomicReference<>();

    public SimpleGameContext(MazeManager mazeManager) {
        this.mazeManager = mazeManager;
    }

    @Override
    public MazeManager getMazeManager() {
        return mazeManager;
    }

    private final ConcurrentMap<String, PlayerSession> playerSessions = new ConcurrentHashMap<>();

    @Override
    public PlayerSession getPlayerSession(@NotNull UserId userId) {
        // Сначала проверяем в памяти
        PlayerSession session = playerSessions.get(userId.getValue());
        if (session != null) {
            return session;
        }

        // Если нет в памяти, пробуем загрузить из файла
        session = loadSessionFromFile(userId);
        if (session != null) {
            playerSessions.put(userId.getValue(), session);
        }

        return session; // может быть null
    }

    private @Nullable PlayerSession loadSessionFromFile(@NotNull UserId userId) {
        Path sessionFile = mazeManager.getMazeDirectory()
                .resolve("users")
                .resolve(userId.getValue())
                .resolve("session.mzpack");

        if (Files.exists(sessionFile)) {
            try {
                return SerializationHelper.loadPlayerSessionFromFile(sessionFile.toString());
            } catch (Exception e) {
                logger.warn("Не удалось загрузить сессию пользователя {}", userId, e);
            }
        }
        return null; // Если файла нет или ошибка загрузки
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
        saveSessionToFile(playerSession.getLogin(), playerSession);
    }

    private void saveSessionToFile(String userId, PlayerSession session) {
        try {
            Path sessionDir = mazeManager.getMazeDirectory().resolve("users").resolve(userId);
            Files.createDirectories(sessionDir);
            Path sessionFile = sessionDir.resolve("session.mzpack");
            SerializationHelper.savePlayerSessionToFile(session, sessionFile.toString());
        } catch (IOException e) {
            logger.error("Не удалось сохранить сессию пользователя {}", userId, e);
        }
    }

    @Override
    public void setCurrentUserId(@NotNull UserId userId) {
        currentUserId.set(userId);
    }

    @Override
    public UserId getCurrentUserId() {
        return currentUserId.get();
    }

}
