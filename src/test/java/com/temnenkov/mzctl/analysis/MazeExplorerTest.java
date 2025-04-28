package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeExplorerTest {

    private Random testRandom;

    @BeforeEach
    void setUp() {
        testRandom = new Random(1L);
    }

    @Test
    void isConnected() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(1, 0), Cell.of(0, 1)));
        maze.addPass(Cell.of(0, 1), Set.of(Cell.of(1, 1)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isConnected(Cell.of(0, 0)));
        assertTrue(mazeExplorer.isConnected(Cell.of(1, 0)));
        assertTrue(mazeExplorer.isConnected(Cell.of(0, 1)));
        assertTrue(mazeExplorer.isConnected(Cell.of(1, 1)));
    }

    @Test
    void notConnected() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(1, 0), Cell.of(0, 1)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

        assertFalse(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isConnected(Cell.of(0, 0)));
        assertFalse(mazeExplorer.isConnected(Cell.of(1, 0)));
        assertFalse(mazeExplorer.isConnected(Cell.of(0, 1)));
        assertFalse(mazeExplorer.isConnected(Cell.of(1, 1)));
    }

    @Test
    void withoutPassesNotConnected() {
        final Maze maze = Maze.of(100, 100);
        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

        assertFalse(mazeExplorer.isConnected());
        for (int i = 0; i < 100; ++i) {
            for (int j = 0; j < 100; ++j) {
                assertFalse(mazeExplorer.isConnected(Cell.of(i, j)));
            }
        }
    }

    /*
    (0,0) — (0,1)
      |       |
    (1,0) — (1,1)
     */
    @Test
    void hasLoop() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(0, 1), Cell.of(1, 0)));
        maze.addPass(Cell.of(1, 1), Set.of(Cell.of(0, 1), Cell.of(1, 0)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);
        assertFalse(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isPerfect());
    }

    /*
    (0,0) — (0,1)
    |
    (1,0)   (1,1)
    */
    @Test
    void noLoop() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(0, 1), Cell.of(1, 0)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);
        assertTrue(mazeExplorer.isAcyclic());
        assertFalse(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isPerfect());
    }

    /*
    (0,0) — (0,1)
    |
    (1,0) - (1,1)
    */
    @Test
    void perfectNoLoop() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(0, 1), Cell.of(1, 0)));
        maze.addPass(Cell.of(1, 0), Set.of(Cell.of(1, 1)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isPerfect());
    }

    /*
    (0,0) — (0,1)

    (1,0) — (1,1)
     */
    @Test
    void disconnectedMaze() {
        final Maze maze = Maze.of(2, 2);
        // два отдельных прохода, не связанных друг с другом
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(0, 1)));
        maze.addPass(Cell.of(1, 0), Set.of(Cell.of(1, 1)));

        MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);
        assertFalse(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic()); // нет циклов в каждой отдельной компоненте
        assertFalse(mazeExplorer.isPerfect()); // не connected, значит не perfect
    }

    /*
    (0,0) — (0,1) — (0,2)
      |       |       |
    (1,0) — (1,1) — (1,2)
      |       |       |
    (2,0) — (2,1) — (2,2)
     */
    @Test
    void testFullConnectedMaze() {
        final Maze maze = Maze.of(3, 3);
        maze.getLinker()
                .link(Cell.of(0, 0), Cell.of(0, 1))
                .link(Cell.of(0, 1), Cell.of(0, 2))
                .link(Cell.of(1, 0), Cell.of(1, 1))
                .link(Cell.of(1, 1), Cell.of(1, 2))
                .link(Cell.of(2, 0), Cell.of(2, 1))
                .link(Cell.of(2, 1), Cell.of(2, 2))
                .link(Cell.of(0, 0), Cell.of(1, 0))
                .link(Cell.of(0, 1), Cell.of(1, 1))
                .link(Cell.of(0, 2), Cell.of(1, 2))
                .link(Cell.of(1, 0), Cell.of(2, 0))
                .link(Cell.of(1, 1), Cell.of(2, 1))
                .link(Cell.of(1, 2), Cell.of(2, 2))
        ;

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

        assertTrue(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isAcyclic());
        assertFalse(mazeExplorer.isPerfect());
        assertEquals(0L, mazeExplorer.deadEndCount());

        assertEquals(4, mazeExplorer.diameter());
        assertEquals(2.0, mazeExplorer.averagePathLength(), 1e-4);
        assertEquals(5L, mazeExplorer.intersectionCount());
        assertEquals(0.5555, mazeExplorer.randomnessScore(), 1e-4);
        assertEquals(0.0, mazeExplorer.balanceScore(), 1e-4);
        assertEquals(1.0, mazeExplorer.symmetryScore(), 1e-4);
    }

    /*
    (0,0) — (0,1) — (0,2)
      |
    (1,0) — (1,1) — (1,2)
      |
    (2,0) — (2,1) — (2,2)
     */
    @Test
    void testShapeEMaze() {
        final Maze maze = Maze.of(3, 3);
        maze.getLinker()
                .link(Cell.of(0, 0), Cell.of(0, 1))
                .link(Cell.of(0, 1), Cell.of(0, 2))
                .link(Cell.of(1, 0), Cell.of(1, 1))
                .link(Cell.of(1, 1), Cell.of(1, 2))
                .link(Cell.of(2, 0), Cell.of(2, 1))
                .link(Cell.of(2, 1), Cell.of(2, 2))
                .link(Cell.of(0, 0), Cell.of(1, 0))
                .link(Cell.of(1, 0), Cell.of(2, 0))
        ;

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isPerfect());
        assertEquals(3L, mazeExplorer.deadEndCount());

        assertEquals(6, mazeExplorer.diameter());
        assertEquals(2.8333, mazeExplorer.averagePathLength(), 1e-4);
        assertEquals(1L, mazeExplorer.intersectionCount());
        assertEquals(0.4444, mazeExplorer.randomnessScore(), 1e-4);
        assertEquals(0.5, mazeExplorer.balanceScore(), 1e-4);
        assertEquals(0.75, mazeExplorer.symmetryScore(), 1e-4);
    }

    /*
    +---+---+---+---+---+
    |           |       |
    +---+---+   +   +   +
    |       |       |   |
    +   +---+   +---+   +
    |   |       |       |
    +   +---+---+   +   +
    |       |       |   |
    +---+   +   +---+   +
    |           |       |
    +---+---+---+---+---+
     */
    @Test
    void testMaze5to5() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("maze-5-5.mzpack")) {
            assertNotNull(is, "Resource maze-5-5.mzpack not found");
            final Maze maze = SerializationHelper.mazeFromMessagePack(is.readAllBytes());
            assertNotNull(maze);

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

            assertTrue(mazeExplorer.isConnected());
            assertTrue(mazeExplorer.isAcyclic());
            assertTrue(mazeExplorer.isPerfect());
            assertEquals(5L, mazeExplorer.deadEndCount());
            assertEquals(18, mazeExplorer.diameter());
            assertEquals(6.8867, mazeExplorer.averagePathLength(), 1e-4);
            assertEquals(3L, mazeExplorer.intersectionCount());
            assertEquals(0.32, mazeExplorer.randomnessScore(), 1e-4);
            assertEquals(0.75, mazeExplorer.balanceScore(), 1e-4);
            assertEquals(0.4583, mazeExplorer.symmetryScore(), 1e-4);
        }
    }

    /*
    +---+---+---+---+---+---+---+---+---+---+
    |                               |   |   |
    +   +---+---+---+---+---+   +---+   +   +
    |           |           |       |   |   |
    +---+---+   +   +---+   +---+   +   +   +
    |       |   |   |       |   |       |   |
    +---+   +   +   +---+   +   +---+---+   +
    |       |   |       |           |       |
    +   +---+   +---+   +---+---+   +   +   +
    |       |       |       |   |   |   |   |
    +---+   +---+   +   +   +   +   +---+   +
    |           |   |   |   |   |       |   |
    +   +---+---+   +---+   +   +---+   +   +
    |   |           |       |       |   |   |
    +   +   +---+---+   +---+---+   +   +   +
    |   |               |           |   |   |
    +   +---+---+---+---+   +   +---+   +   +
    |               |       |           |   |
    +   +---+---+---+   +---+---+---+---+   +
    |                                       |
    +---+---+---+---+---+---+---+---+---+---+
     */
    @Test
    void testMaze10to10() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("maze-10-10.mzpack")) {
            assertNotNull(is, "Resource maze-10-10.mzpack not found");
            final Maze maze = SerializationHelper.mazeFromMessagePack(is.readAllBytes());
            assertNotNull(maze);

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

            assertTrue(mazeExplorer.isConnected());
            assertTrue(mazeExplorer.isAcyclic());
            assertTrue(mazeExplorer.isPerfect());
            assertEquals(11L, mazeExplorer.deadEndCount());
            assertEquals(70, mazeExplorer.diameter());
            assertEquals(25.2804, mazeExplorer.averagePathLength(), 1e-4);
            assertEquals(9L, mazeExplorer.intersectionCount());
            assertEquals(0.2, mazeExplorer.randomnessScore(), 1e-4);
            assertEquals(0.9, mazeExplorer.balanceScore(), 1e-4);
            assertEquals(0.6162, mazeExplorer.symmetryScore(), 1e-4);
        }
    }

    @Test
    void testAveragePathLengthSingleCell() {
        Maze maze = Maze.of(1); // лабиринт с одной комнатой
        MazeExplorer mazeExplorer = new MazeExplorer(maze, testRandom);

        assertEquals(0L, mazeExplorer.deadEndCount());
        assertEquals(0, mazeExplorer.diameter());
        assertEquals(0.0, mazeExplorer.averagePathLength(), 1e-4);
        assertEquals(0L, mazeExplorer.intersectionCount());
        assertEquals(0.0, mazeExplorer.randomnessScore(), 1e-4);
        assertEquals(1.0, mazeExplorer.balanceScore(), 1e-4);
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, mazeExplorer::symmetryScore);
        assertTrue(thrown.getMessage().contains("Only 2-dimensional mazes supported"));
    }

}
