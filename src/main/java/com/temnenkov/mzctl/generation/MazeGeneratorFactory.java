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
            case SIDEWINDER -> new SidewinderMazeGenerator(mazeDim, random);
            case RANDOMIZED_PRIM -> new RandomizedPrimMazeGenerator(mazeDim, random);
            case RANDOMIZED_KRUSKAL -> new RandomizedKruskalMazeGenerator(mazeDim, random);
            case HUNT_AND_KILL -> new HuntAndKillMazeGenerator(mazeDim, random);
        };
    }

        /**
         * Создает генератор лабиринтов Growing Tree с заданной стратегией и mixedProbability.
         *
         * @param mazeDim          размерность лабиринта
         * @param strategy         стратегия Growing Tree
         * @param mixedProbability вероятность для MIXED стратегии
         * @return генератор лабиринтов Growing Tree
         */
        @NotNull
        public MazeGenerator createGrowingTree(@NotNull MazeDim mazeDim,
                @NotNull GrowingTreeMazeGenerator.Strategy strategy,
                double mixedProbability) {
            logger.info("Creating Growing Tree maze generator: strategy={}, mixedProbability={}, mazeDim={}",
                    strategy, mixedProbability, mazeDim);
            return new GrowingTreeMazeGenerator(mazeDim, random, strategy, mixedProbability);
        }

        public enum Algo {
            RECURSIVE_BACKTRACKER,
            RECURSIVE_DIVISION,
            BINARY_TREE,
            SIDEWINDER,
            RANDOMIZED_PRIM,
            RANDOMIZED_KRUSKAL,
            HUNT_AND_KILL
        }
}

