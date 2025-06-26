package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExploreAllRoomsStateTest {

    private Maze maze;
    private ExploreAllRoomsState state;

    @BeforeEach
    void setup() {
        maze = MazeFactory.createFullConnectedMaze(MazeDim.of(2, 2)); // Лабиринт 2x2 (4 комнаты)
        state = new ExploreAllRoomsState(maze);
    }

    @Test
    void testInitialConditions() {
        assertEquals(0, state.visitedCount());
        assertEquals(4, state.remainingCount());
        assertFalse(state.allVisited());
    }

    @Test
    void testMarkVisited() {
        Cell cell = Cell.of(0, 0);
        state.markVisited(cell);

        assertTrue(state.isVisited(cell));
        assertEquals(1, state.visitedCount());
        assertEquals(3, state.remainingCount());
        assertFalse(state.allVisited());
    }

    @Test
    void testAllVisited() {
        for (Cell cell : maze) {
            state.markVisited(cell);
        }

        assertEquals(4, state.visitedCount());
        assertEquals(0, state.remainingCount());
        assertTrue(state.allVisited());
    }

    @Test
    void testMultipleVisits() {
        Cell cell = Cell.of(1, 1);
        state.markVisited(cell);
        state.markVisited(cell); // повторно, ничего не должно измениться

        assertEquals(1, state.visitedCount());
        assertEquals(3, state.remainingCount());
    }
}