package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class UserId {
    private final String value;

    public UserId(@NotNull String value) {
        this.value = Objects.requireNonNull(value, "UserId value cannot be null");
    }

    @NotNull
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "UserId{" + value + '}';
    }
}