package com.temnenkov.mzctl.commands.util;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.model.PlayerSession;
import org.jetbrains.annotations.Nullable;

public final class CommandUtils {
    private CommandUtils() {
        throw new UnsupportedOperationException("CommandUtils cannot be instantiated");
    }

    public static @Nullable PlayerSession loadValidPlayerSession(GameContext gameContext) {
        // временно один логин
        final PlayerSession playerSession = gameContext.getPlayerSession("test");
        if (playerSession == null || playerSession.getMaze() == null || playerSession.getPlayerStateND() == null) {
            System.out.println("Ошибка: лабиринт не загружен. Сначала загрузите лабиринт командой 'load-maze'.");
            return null;
        }
        return playerSession;
    }


}
