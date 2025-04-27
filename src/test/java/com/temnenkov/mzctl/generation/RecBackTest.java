package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.agent.MazeExplorer;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecBackTest {

    @Test
    void generateMaze() {
        //given
        final Random random = new Random(1L);
        final RecBack recBack = new RecBack(MazeDim.of(4, 4), random);
        //when
        final Maze maze = recBack.generateMaze();
        //then
        assertNotNull(maze);
        final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
        assertTrue(mazeExplorer.isPerfect());
        System.out.println("deadEnds: " + mazeExplorer.deadEndCount());
    }
}