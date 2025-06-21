package com.temnenkov.mzctl.context;

import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.model.UserId;

public interface GameContext {
    MazeManager getMazeManager();
    PlayerSession getPlayerSession(UserId userId);
    void createPlayerSession(PlayerSession playerSession);
    void updatePlayerSession(PlayerSession playerSession);
    void setCurrentUserId(UserId userId);
    UserId getCurrentUserId();
}
