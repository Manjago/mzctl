package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.MazeDim;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты для {@link MazeGeneratorFactory}.
 */
class MazeGeneratorFactoryTest {

    private final Random random = new Random(42);
    private final MazeDim mazeDim = MazeDim.of(5, 5);

    @Test
    void shouldCreateRecursiveBacktrackerGenerator() {
        MazeGeneratorFactory factory = new MazeGeneratorFactory(random);
        MazeGenerator generator = factory.create(MazeGeneratorFactory.Algo.RECURSIVE_BACKTRACKER, mazeDim);
        assertNotNull(generator);
        assertInstanceOf(RecursiveBacktracker.class, generator);
    }

    @Test
    void shouldCreateRecursiveDivisionGenerator() {
        MazeGeneratorFactory factory = new MazeGeneratorFactory(random);
        MazeGenerator generator = factory.create(MazeGeneratorFactory.Algo.RECURSIVE_DIVISION, mazeDim);
        assertNotNull(generator);
        assertInstanceOf(RecursiveDivision.class, generator);
    }
}