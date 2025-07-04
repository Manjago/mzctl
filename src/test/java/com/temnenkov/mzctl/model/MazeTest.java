package com.temnenkov.mzctl.model;

import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeTest {

    @Test
    void testAddPassAndCanPass() {
        Maze maze = new Maze(new MazeDim(List.of(3, 3)));
        Cell cellA = Cell.of(0, 0);
        Cell cellB = Cell.of(0, 1);
        Cell cellC = Cell.of(1, 1);

        maze.addPass(cellA, Set.of(cellB));

        assertTrue(maze.canPass(cellA, cellB));
        assertTrue(maze.canPass(cellB, cellA));
        assertFalse(maze.canPass(cellA, cellC));
    }

    @Test
    void testMazeSaveAndLoad(@TempDir Path tempDir) {
        Maze maze = new Maze(new MazeDim(List.of(3, 3)));
        Cell cellA = Cell.of(0, 0);
        Cell cellB = Cell.of(0, 1);
        maze.addPass(cellA, Set.of(cellB));

        final Path file = tempDir.resolve("test.mzpack");
        SerializationHelper.saveMazeToFile(maze, file.toString());
        Maze loadedMaze = SerializationHelper.loadMazeFromFile(file.toString());

        assertNotNull(loadedMaze);
        assertTrue(loadedMaze.canPass(cellA, cellB));
        assertTrue(loadedMaze.canPass(cellB, cellA));

        assertEquals(maze, loadedMaze);
    }

    @Test
    void totalCellCount() {
        final Maze maze = MazeFactory.createNotConnectedMaze(1, 2, 3, 4);
        assertEquals(24, maze.totalCellCount());
    }

    @Test
    void totalCellCountOneDimension() {
        final Maze maze = MazeFactory.createNotConnectedMaze(5);
        assertEquals(5, maze.totalCellCount());
    }

    @Test
    void totalCellCountTwoDimensions() {
        final Maze maze = MazeFactory.createNotConnectedMaze(3, 3);
        assertEquals(9, maze.totalCellCount());
    }

    @Test
    void testLoop() {
        final Maze maze = MazeFactory.createNotConnectedMaze(2, 3);

        final List<Cell> expected = List.of(
                Cell.of(0, 0),
                Cell.of(0, 1),
                Cell.of(0, 2),
                Cell.of(1, 0),
                Cell.of(1, 1),
                Cell.of(1, 2)
                );

        assertMazeCells(maze, expected);
    }

    @Test
    void testLoopThreeDimensions() {
        final Maze maze = MazeFactory.createNotConnectedMaze(2, 2, 2);

        final List<Cell> expected = List.of(
                Cell.of(0, 0, 0),
                Cell.of(0, 0, 1),
                Cell.of(0, 1, 0),
                Cell.of(0, 1, 1),
                Cell.of(1, 0, 0),
                Cell.of(1, 0, 1),
                Cell.of(1, 1, 0),
                Cell.of(1, 1, 1)
        );

        assertMazeCells(maze, expected);
    }

    private void assertMazeCells(@NotNull Maze maze, List<Cell> expected) {
        // проверка итератора
        List<Cell> fromIterator = new ArrayList<>();
        for (Cell cell : maze) {
            fromIterator.add(cell);
        }
        assertEquals(expected, fromIterator);

        // проверка Stream
        List<Cell> fromStream = maze.stream().toList();
        assertEquals(expected, fromStream);
    }
}