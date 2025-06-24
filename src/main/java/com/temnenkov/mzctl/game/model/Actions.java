package com.temnenkov.mzctl.game.model;

public final class Actions {
    public static final String GO_AHEAD = "⬆️ Иди вперёд";
    public static final String LEFT = "⬅️ Поворот влево";
    public static final String WHERE_AM_I = "❓ Где я?";
    public static final String RIGHT = "➡️ Поворот вправо";
    public static final String BACK = "🔄 Разворот назад";

    private Actions() {
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass().getSimpleName());
    }
}
