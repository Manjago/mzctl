package com.temnenkov.mzctl.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.temnenkov.mzctl.model.serialize.CellKeyDeserializer;
import com.temnenkov.mzctl.model.serialize.CellKeySerializer;
import org.junit.jupiter.api.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MazeTest {

    @Test
    void testAddPassAndCanPass() {
        Maze maze = new Maze();
        Cell cellA = new Cell(List.of(0, 0));
        Cell cellB = new Cell(List.of(0, 1));
        Cell cellC = new Cell(List.of(1, 1));

        maze.addPass(cellA, Set.of(cellB));

        assertTrue(maze.canPass(cellA, cellB));
        assertTrue(maze.canPass(cellB, cellA));
        assertFalse(maze.canPass(cellA, cellC));
    }

    @Test
    void testMessagePackSerialization() throws Exception {
        Maze maze = new Maze();
        Cell cellA = new Cell(List.of(0, 0));
        Cell cellB = new Cell(List.of(0, 1));
        maze.addPass(cellA, Set.of(cellB));

        ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

        SimpleModule module = new SimpleModule();
        module.addKeySerializer(Cell.class, new CellKeySerializer());
        module.addKeyDeserializer(Cell.class, new CellKeyDeserializer());
        mapper.registerModule(module);

        byte[] bytes = mapper.writeValueAsBytes(maze);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        Maze loadedMaze = mapper.readValue(bytes, Maze.class);
        assertNotNull(loadedMaze);
        assertTrue(loadedMaze.canPass(cellA, cellB));
        assertTrue(loadedMaze.canPass(cellB, cellA));
    }
}