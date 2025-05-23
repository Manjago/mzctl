package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

@FunctionalInterface
public interface RandomProvider {
    @NotNull Random getRandom();
}
