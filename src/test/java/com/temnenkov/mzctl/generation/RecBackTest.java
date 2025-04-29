package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.analysis.MazeExplorer;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import com.temnenkov.mzctl.visualization.MazeImageVisualizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecBackTest {

    @Test
    void generateMazeAndShowAsAscii() {
        //given
        final Random random = new SecureRandom();
        final RecursiveBacktracker recursiveBacktracker = new RecursiveBacktracker(MazeDim.of(5, 5), random);
        //when
        final Maze maze = recursiveBacktracker.generateMaze();
        //then
        assertNotNull(maze);
        final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
        assertTrue(mazeExplorer.isPerfect());
        System.out.println("deadEnds: " + mazeExplorer.deadEndCount());

        new MazeAsciiVisualizer(maze).printMaze();
    }

    @Test
    void generateMazeAndShowAsImage() throws IOException {
        //given
        final Random random = new SecureRandom();
        final RecursiveBacktracker recursiveBacktracker = new RecursiveBacktracker(MazeDim.of(100, 100), random);
        //when
        final Maze maze = recursiveBacktracker.generateMaze();
        //then
        assertNotNull(maze);
        final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
        assertTrue(mazeExplorer.isPerfect());
        System.out.println("deadEnds: " + mazeExplorer.deadEndCount());

        new MazeImageVisualizer(maze, 20, 2).saveMazeImage("target/maze.png");
    }
}