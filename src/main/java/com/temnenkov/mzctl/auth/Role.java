package com.temnenkov.mzctl.auth;

public enum Role {
    PLAYER,      // обычный игрок
    ADMIN,       // администратор (может видеть лабиринт)
    DEVELOPER    // разработчик (может видеть лабиринт, отлаживать и т.д.)
}