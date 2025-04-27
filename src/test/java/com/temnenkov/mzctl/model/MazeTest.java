package com.temnenkov.mzctl.model;

import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
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
        Cell cellA = new Cell(List.of(0, 0));
        Cell cellB = new Cell(List.of(0, 1));
        Cell cellC = new Cell(List.of(1, 1));

        maze.addPass(cellA, Set.of(cellB));

        assertTrue(maze.canPass(cellA, cellB));
        assertTrue(maze.canPass(cellB, cellA));
        assertFalse(maze.canPass(cellA, cellC));
    }

    @Test
    void testMessagePackSerialization() {
        Maze maze = new Maze(new MazeDim(List.of(3, 3)));
        Cell cellA = new Cell(List.of(0, 0));
        Cell cellB = new Cell(List.of(0, 1));
        maze.addPass(cellA, Set.of(cellB));

        byte[] bytes = SerializationHelper.mazeToMessagePack(maze);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        Maze loadedMaze = SerializationHelper.mazeFromMessagePack(bytes);
        assertNotNull(loadedMaze);
        assertTrue(loadedMaze.canPass(cellA, cellB));
        assertTrue(loadedMaze.canPass(cellB, cellA));

        assertEquals(maze, loadedMaze);
    }

    @Test
    void testMazeSaveAndLoad(@TempDir Path tempDir) {
        Maze maze = new Maze(new MazeDim(List.of(3, 3)));;
        Cell cellA = new Cell(List.of(0, 0));
        Cell cellB = new Cell(List.of(0, 1));
        maze.addPass(cellA, Set.of(cellB));

        final Path file = tempDir.resolve("test.mzpack");
        SerializationHelper.saveMazeToFile(maze, file.toString());
        Maze loadedMaze = SerializationHelper.loadMazeFromFile(file.toString());

        assertNotNull(loadedMaze);
        assertTrue(loadedMaze.canPass(cellA, cellB));
        assertTrue(loadedMaze.canPass(cellB, cellA));

        assertEquals(maze, loadedMaze);
    }
}