package com.temnenkov.mzctl.commands.util;

import com.temnenkov.mzctl.context.GameContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameContextHelper {
    private GameContextHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Получить идентификатор пользователя с fallback в контекст
     * @param gameContext контекст
     * @param candidate идентификатор пользователя, может быть null, если так, то лезем в контекст
     * @return идентификатор пользователя - переданный или из контекста
     */
    @Nullable
    public static String getUserId(@NotNull GameContext gameContext, @Nullable String candidate) {
        return candidate != null ? candidate : gameContext.getCurrentUserId();
    }
}
