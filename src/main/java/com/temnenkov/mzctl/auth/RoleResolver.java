package com.temnenkov.mzctl.auth;

import com.temnenkov.mzctl.model.UserId;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RoleResolver {
    private static final Map<String, Role> USER_ROLES = Map.of(
            "tester", Role.DEVELOPER
            // здесь можно легко добавлять новых пользователей
    );

    public Role roleByUserId(@NotNull UserId userId) {
        return USER_ROLES.getOrDefault(userId.getValue().toLowerCase(), Role.PLAYER);
    }
}
