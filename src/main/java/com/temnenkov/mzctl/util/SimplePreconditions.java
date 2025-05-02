package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimplePreconditions {
    private SimplePreconditions() {
        throw new UnsupportedOperationException("Do not instantiate by reflection!");
    }

    @Contract(value = "null, _ -> fail; !null, _ -> param1", pure = true)
    public static <T> @NotNull T checkNotNull(@Nullable T reference, @NotNull String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
        return reference;
    }

    public static void checkState(boolean condition, @NotNull String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
