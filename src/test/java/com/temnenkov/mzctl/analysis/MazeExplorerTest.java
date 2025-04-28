package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeExplorerTest {

    @Test
    void isConnected() {
        final Maze maze = Maze.of(2, 2);
        maze.addPass(Cell.of(0, 0), Set.of(Cell.of(1, 0), Cell.of(0, 1)));
        maze.addPass(Cell.of(0, 1), Set.of(Cell.of(1, 1)));

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

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

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

        assertFalse(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isConnected(Cell.of(0, 0)));
        assertFalse(mazeExplorer.isConnected(Cell.of(1, 0)));
        assertFalse(mazeExplorer.isConnected(Cell.of(0, 1)));
        assertFalse(mazeExplorer.isConnected(Cell.of(1, 1)));
    }

    @Test
    void withoutPassesNotConnected() {
        final Maze maze = Maze.of(100, 100);
        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

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

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));
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

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));
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

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));
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

        MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));
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

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

        assertTrue(mazeExplorer.isConnected());
        assertFalse(mazeExplorer.isAcyclic());
        assertFalse(mazeExplorer.isPerfect());
        assertEquals(0L, mazeExplorer.deadEndCount());
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

        final MazeExplorer mazeExplorer = new MazeExplorer(maze, new Random(1L));

        assertTrue(mazeExplorer.isConnected());
        assertTrue(mazeExplorer.isAcyclic());
        assertTrue(mazeExplorer.isPerfect());
        assertEquals(3L, mazeExplorer.deadEndCount());
    }

}