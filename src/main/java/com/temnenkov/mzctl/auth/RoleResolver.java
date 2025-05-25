package com.temnenkov.mzctl.auth;

public class RoleResolver {
    public Role roleByUserLogin(String login) {
        // в телеграм такого id не будет никогда, для консольного отладочного режима такой уровень секьюрности ок
        if ("tester".equalsIgnoreCase(login)) {
            return Role.DEVELOPER;
        } else {
            return Role.PLAYER;
        }
    }
}
