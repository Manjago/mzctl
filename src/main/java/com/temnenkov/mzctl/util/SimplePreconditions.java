package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimplePreconditions {
    private SimplePreconditions() {
        throw new UnsupportedOperationException("Do not instantiate by reflection!");
    }

    @Contract(value = "null, _, _ -> fail; !null, _, _ -> param1", pure = true)
    public static <T> @NotNull T checkNotNull(@Nullable T reference, @NotNull String paramName, @NotNull String methodName) {
        if (reference == null) {
            throw new IllegalArgumentException("Argument for @NotNull parameter '"
                    + paramName + "' of " + methodName + " must not be null");
        }
        return reference;
    }

    public static void checkState(boolean condition, @NotNull String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkArgument(boolean condition, @NotNull String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> @NotNull T checkContains(@Nullable T reference, @NotNull String paramName, @NotNull String methodName, @NotNull Object key) {
        if (reference == null) {
            throw new IllegalArgumentException("Element '" + key + "' not found for parameter '" + paramName + "' of " + methodName);
        }
        return reference;
    }
}