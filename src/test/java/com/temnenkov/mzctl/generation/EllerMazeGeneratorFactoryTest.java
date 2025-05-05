package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.analysis.MazeExplorer;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.util.SimpleStopWatch;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import com.temnenkov.mzctl.visualization.MazeImageVisualizer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EllerMazeGeneratorFactoryTest {

    private MazeGeneratorFactory mazeGeneratorFactory;

    @BeforeEach
    void setUp() {
        mazeGeneratorFactory = new MazeGeneratorFactory(new Random(42L));
    }

    static @NotNull Stream<Arguments> mazeGenerationStrategies() {
        return Stream.of(
                Arguments.of(MazeDim.of(5, 5), true, true),
                Arguments.of(MazeDim.of(20, 20), true, true),
                Arguments.of(MazeDim.of(40, 40), false, true),
                Arguments.of(MazeDim.of(5, 5, 5), false, false),
                Arguments.of(MazeDim.of(20, 20, 20), false, false),
                Arguments.of(MazeDim.of(5, 5, 5, 5), false, false),
                Arguments.of(MazeDim.of(10, 10, 10, 10), false, false)
        );
    }

    @ParameterizedTest(name = "generateMaze mazeDim={0}, drawAscii={1}, drawPng={2}")
    @MethodSource("mazeGenerationStrategies")
    void generateMaze(@NotNull MazeDim mazeDim, boolean drawAscii, boolean drawPng) throws IOException {
        System.out.printf("Generating Maze mazeDim=%s", mazeDim.display());

        // given
        final MazeGenerator generator =
        mazeGeneratorFactory.create(MazeGeneratorFactory.Algo.ELLER, mazeDim);

        // when
        final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
        final Maze maze = generator.generateMaze();
        final long genDurationMs = genWatch.elapsed();

        // then
        assertNotNull(maze);

        if (drawAscii) {
            new MazeAsciiVisualizer(maze).printMaze();
        }

        if (drawPng) {
            new MazeImageVisualizer(maze).saveMazeImage(
                    String.format("target/eller-%s.png", mazeDim.display()));
        }

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new SecureRandom());
        final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isPerfect());
        final String report = mazeExplorer.report();
        final long exploreDurationMs = exploreWatch.elapsed();

        System.out.printf("""
                    Dimension: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", mazeDim.display(), genDurationMs, exploreDurationMs, report);

    }
}