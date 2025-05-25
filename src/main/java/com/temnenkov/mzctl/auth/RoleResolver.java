package com.temnenkov.mzctl.auth;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RoleResolver {
    private static final Map<String, Role> USER_ROLES = Map.of(
            "tester", Role.DEVELOPER
            // здесь можно легко добавлять новых пользователей
    );

    public Role roleByUserLogin(@NotNull String login) {
        return USER_ROLES.getOrDefault(login.toLowerCase(), Role.PLAYER);
    }
}
