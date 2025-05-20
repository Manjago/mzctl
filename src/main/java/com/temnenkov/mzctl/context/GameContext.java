package com.temnenkov.mzctl.context;

import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.PlayerSession;

public interface GameContext {
    MazeManager getMazeManager();
    PlayerSession getPlayerSession(String login);
    void createPlayerSession(PlayerSession playerSession);
    void updatePlayerSession(PlayerSession playerSession);
    void setCurrentUserId(String userId);
    String getCurrentUserId();
}
