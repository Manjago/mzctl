package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.analysis.MazeExplorer;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.util.SimpleStopWatch;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import com.temnenkov.mzctl.visualization.MazeImageVisualizer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrowingTreeMazeGeneratorTest {

    static @NotNull Stream<Arguments> mazeGenerationStrategies() {
        return Stream.of(
                Arguments.of(GrowingTreeMazeGenerator.Strategy.NEWEST, 0.0),
                Arguments.of(GrowingTreeMazeGenerator.Strategy.OLDEST, 0.0),
                Arguments.of(GrowingTreeMazeGenerator.Strategy.RANDOM, 0.0),
                Arguments.of(GrowingTreeMazeGenerator.Strategy.MIXED, 0.25),
                Arguments.of(GrowingTreeMazeGenerator.Strategy.MIXED, 0.5),
                Arguments.of(GrowingTreeMazeGenerator.Strategy.MIXED, 0.75)
        );
    }

    @ParameterizedTest(name = "generateMaze strategy={0}, mixedProbability={1}")
    @MethodSource("mazeGenerationStrategies")
    void generateMaze(GrowingTreeMazeGenerator.Strategy strategy, double mixedProbability) throws IOException {
        System.out.printf("Generating Maze strategy=%s, mixedProbability=%.2f%n", strategy, mixedProbability);

        // given
        final Random random = new SecureRandom();
        final GrowingTreeMazeGenerator generator = new GrowingTreeMazeGenerator(
                MazeDim.of(20, 20), random, strategy, mixedProbability);

        // when
        final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
        final Maze maze = generator.generateMaze();
        final long genDurationMs = genWatch.elapsed();

        // then
        assertNotNull(maze);

        new MazeAsciiVisualizer(maze).printMaze();
        new MazeImageVisualizer(maze).saveMazeImage(
                String.format("target/growing-tree-%s-%.2f.png", strategy.name().toLowerCase(), mixedProbability));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
        final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isPerfect());
        final String report = mazeExplorer.report();
        final long exploreDurationMs = exploreWatch.elapsed();

        System.out.printf("""
                    Strategy: %s
                    MixedProbability: %.2f
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", strategy, mixedProbability, genDurationMs, exploreDurationMs, report);

    }
}