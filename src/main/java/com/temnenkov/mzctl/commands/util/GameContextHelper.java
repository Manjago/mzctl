package com.temnenkov.mzctl.commands.util;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.model.UserId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameContextHelper {
    private GameContextHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Получить идентификатор пользователя с fallback в контекст
     * @param gameContext контекст
     * @param userIdString идентификатор пользователя, может быть null, если так, то лезем в контекст
     * @return идентификатор пользователя - переданный или из контекста
     */
    @Nullable
    public static UserId resolveUserId(@NotNull GameContext gameContext, @Nullable String userIdString) {
        if (userIdString != null) {
            return new UserId(userIdString);
        }
        final UserId currentUserId = gameContext.getCurrentUserId();
        if (currentUserId == null) {
            System.out.println("Ошибка: сначала авторизуйтесь через команду login");
            return null;
        }
        return currentUserId;
    }
}
