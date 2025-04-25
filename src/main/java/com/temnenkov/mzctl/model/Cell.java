package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Cell(@NotNull List<Integer> coordinates) {
    public Cell(@NotNull List<Integer> coordinates) {
        this.coordinates = List.copyOf(coordinates);
    }
}
