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

class HuntAndKillMazeGeneratorTest {
    @Test
    void generateMaze2d20to20AndShow() throws IOException {
        //given
        final Random random = new SecureRandom();
        final HuntAndKillMazeGenerator randomizedKruskalMazeGenerator = new HuntAndKillMazeGenerator(MazeDim.of(20, 20), random);
        //when
        final Maze maze = randomizedKruskalMazeGenerator.generateMaze();
        //then
        assertNotNull(maze);

        new MazeAsciiVisualizer(maze).printMaze();
        new MazeImageVisualizer(maze).saveMazeImage("target/randomized-kruskal-2.png");

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isPerfect());
    }

    @Test
    void generateMaze2d5to5AndShow() throws IOException {
        //given
        final Random random = new SecureRandom();
        final HuntAndKillMazeGenerator huntAndKillMazeGenerator = new HuntAndKillMazeGenerator(MazeDim.of(5, 5), random);
        //when
        final Maze maze = huntAndKillMazeGenerator.generateMaze();
        //then
        assertNotNull(maze);

        new MazeAsciiVisualizer(maze).printMaze();
        new MazeImageVisualizer(maze, 20, 2).saveMazeImage("target/randomized-kruskal-1.png");

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isPerfect());
    }

}