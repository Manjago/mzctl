package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Простой секундомер для измерения времени выполнения.
 * Используется для замера времени в миллисекундах.
 */
public class SimpleStopWatch {
    private final long startTime = System.nanoTime();

    private SimpleStopWatch() {
    }

    /**
     * Создает и запускает новый секундомер.
     *
     * @return запущенный секундомер
     */
    @Contract(" -> new")
    public static @NotNull SimpleStopWatch createStarted() {
        return new SimpleStopWatch();
    }

    /**
     * Возвращает прошедшее время в миллисекундах, не останавливая секундомер.
     *
     * @return прошедшее время в миллисекундах
     */
    public long elapsed() {
        return (System.nanoTime() - startTime) / 1_000_000;
    }
}
