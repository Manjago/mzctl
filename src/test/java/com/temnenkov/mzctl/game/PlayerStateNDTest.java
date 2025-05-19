package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerStateNDTest {

    @Test
    void testCreatePlayerStateND() {
        Cell position = Cell.of(1, 2);
        Facing facing = Facing.NORTH;

        PlayerStateND player = new PlayerStateND(position, facing);

        assertEquals(position, player.getPosition());
        assertEquals(facing, player.getFacing());
    }

    @Test
    void testCreatePlayerStateNDInvalidSize() {
        Cell position = Cell.of(1, 2, 3);
        Facing facing = Facing.NORTH; // двумерное направление

        assertThrows(IllegalArgumentException.class, () -> new PlayerStateND(position, facing));
    }

    @Test
    void testMoveForward() {
        PlayerStateND player = new PlayerStateND(Cell.ofRowAndColumn(2, 2), Facing.NORTH);
        player.moveForward();

        assertEquals(Cell.ofRowAndColumn(1, 2), player.getPosition());
    }

    @Test
    void testTurn() {
        PlayerStateND player = new PlayerStateND(Cell.ofRowAndColumn(0, 0), Facing.NORTH);
        player.turn(Facing.Dimension.Y, Facing.Dimension.X);

        assertEquals(Facing.EAST, player.getFacing());
    }

    @Test
    void testRotateClockwise2D() {
        PlayerStateND player = new PlayerStateND(Cell.of(0, 0), Facing.NORTH);
        player.rotateClockwise2D();

        assertEquals(Facing.EAST, player.getFacing());

        player.rotateClockwise2D();
        assertEquals(Facing.SOUTH, player.getFacing());
    }

    @Test
    void testRotateCounterClockwise2D() {
        PlayerStateND player = new PlayerStateND(Cell.of(0, 0), Facing.NORTH);
        player.rotateCounterClockwise2D();

        assertEquals(Facing.WEST, player.getFacing());

        player.rotateCounterClockwise2D();
        assertEquals(Facing.SOUTH, player.getFacing());
    }

    @Test
    void testOpposite() {
        PlayerStateND player = new PlayerStateND(Cell.of(0, 0), Facing.NORTH);
        player.opposite();

        assertEquals(Facing.SOUTH, player.getFacing());
    }

    @Test
    void testToString() {
        PlayerStateND player = new PlayerStateND(Cell.of(1, 1), Facing.EAST);
        assertEquals("PlayerStateND{position=Cell[1, 1], facing=Facing[X(+)]}", player.toString());
    }

    @Test
    void testRotateClockwise2DInvalidDimension() {
        PlayerStateND player = new PlayerStateND(Cell.of(0), Facing.of(Facing.AxisDirection.POSITIVE));
        assertThrows(IllegalStateException.class, player::rotateClockwise2D);
    }

    @Test
    void testRotateCounterClockwise2DInvalidDimension() {
        PlayerStateND player = new PlayerStateND(Cell.of(0), Facing.of(Facing.AxisDirection.POSITIVE));
        assertThrows(IllegalStateException.class, player::rotateCounterClockwise2D);
    }
}