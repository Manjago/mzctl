package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.MazeDim;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Фабрика для создания генераторов лабиринтов.
 */
public class MazeGeneratorFactory {
    private static final Logger logger = LoggerFactory.getLogger(MazeGeneratorFactory.class);
    private final Random random;

    /**
     * Создает фабрику с заданным генератором случайных чисел.
     *
     * @param random генератор случайных чисел
     */
    public MazeGeneratorFactory(Random random) {
        this.random = random;
    }

    /**
     * Создает генератор лабиринтов по указанному алгоритму и размерности.
     *
     * @param algo    алгоритм генерации
     * @param mazeDim размерность лабиринта
     * @return генератор лабиринтов
     * @throws IllegalArgumentException если алгоритм не поддерживается
     */
    @NotNull
    public MazeGenerator create(@NotNull Algo algo, @NotNull MazeDim mazeDim) {
        logger.info("Creating maze generator: {} by parameter {}", algo, mazeDim);
        return switch (algo) {
            case RECURSIVE_BACKTRACKER -> new RecursiveBacktracker(mazeDim, random);
            case RECURSIVE_DIVISION -> new RecursiveDivision(mazeDim, random);
            case BINARY_TREE -> new BinaryTreeMazeGenerator(mazeDim, random);
        };
    }

    /**
     * Перечисление поддерживаемых алгоритмов генерации лабиринтов.
     */
    public enum Algo {
        RECURSIVE_BACKTRACKER, RECURSIVE_DIVISION, BINARY_TREE
    }
}
