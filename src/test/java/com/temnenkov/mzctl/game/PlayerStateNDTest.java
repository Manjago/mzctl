package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.model.Cell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerStateNDTest {

    @Test
    void testInitialPlayerState() {
        Cell startPosition = Cell.of(0, 0);
        Facing startFacing = Facing.of(0, 1);

        PlayerStateND player = new PlayerStateND(startPosition, startFacing);

        assertEquals(startPosition, player.getPosition());
        assertEquals(startFacing, player.getFacing());
    }

    @Test
    void testMoveForward() {
        Cell startPosition = Cell.of(2, 3);
        Facing startFacing = Facing.of(0, 1); // смотрим вправо

        PlayerStateND player = new PlayerStateND(startPosition, startFacing);
        player.moveForward();

        assertEquals(Cell.of(2, 4), player.getPosition());
    }

    @Test
    void testTurn() {
        Cell startPosition = Cell.of(2, 3);
        Facing startFacing = Facing.of(0, 1); // смотрим вправо

        PlayerStateND player = new PlayerStateND(startPosition, startFacing);

        // Поворот вправо (в плоскости 0-1)
        player.turn(0, 1);
        assertEquals(Facing.of(-1, 0), player.getFacing());

        // Поворот еще раз вправо
        player.turn(0, 1);
        assertEquals(Facing.of(0, -1), player.getFacing());
    }

    @Test
    void testMultipleMovesAndTurns() {
        Cell startPosition = Cell.of(1, 1);
        Facing startFacing = Facing.of(0, 1); // смотрим вправо

        PlayerStateND player = new PlayerStateND(startPosition, startFacing);

        player.moveForward(); // (1, 2)
        assertEquals(Cell.of(1, 2), player.getPosition());

        player.turn(0, 1); // смотрим вверх
        assertEquals(Facing.of(-1, 0), player.getFacing());

        player.moveForward(); // (0, 2)
        assertEquals(Cell.of(0, 2), player.getPosition());

        player.turn(0, 1); // смотрим влево
        assertEquals(Facing.of(0, -1), player.getFacing());

        player.moveForward(); // (0, 1)
        assertEquals(Cell.of(0, 1), player.getPosition());
    }

    @Test
    void testInvalidInitialization() {
        Cell startPosition = Cell.of(0, 0);
        Facing invalidFacing = Facing.of(1, 0, 0); // направление другого размера (3D вместо 2D)

        assertThrows(IllegalArgumentException.class, () -> new PlayerStateND(startPosition, invalidFacing));
    }
}