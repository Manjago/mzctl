package com.temnenkov.mzctl.game.model;

import com.temnenkov.mzctl.model.Cell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacingTest {

    @Test
    void testCreateFacing() {
        Facing facing = Facing.of(0, 1);
        assertArrayEquals(new int[]{0, 1}, facing.getDirections());
    }

    @Test
    void testCreateFacingWithZeroVector() {
        assertThrows(IllegalArgumentException.class, () -> Facing.of(0, 0));
    }

    @Test
    void testMoveForward() {
        Facing facing = Facing.of(0, 1);
        Cell initialCell = Cell.of(2, 3);
        Cell resultCell = facing.moveForward(initialCell);
        assertEquals(Cell.of(2, 4), resultCell);
    }

    @Test
    void testTurn() {
        // Начальное направление (смотрим вправо)
        Facing facing = Facing.of(0, 1);

        // Поворот на 90 градусов (в плоскости 0-1)
        Facing turnedFacing = facing.turn(0, 1);
        assertArrayEquals(new int[]{-1, 0}, turnedFacing.getDirections());

        // Еще один поворот на 90 градусов
        Facing turnedAgainFacing = turnedFacing.turn(0, 1);
        assertArrayEquals(new int[]{0, -1}, turnedAgainFacing.getDirections());
    }

    @Test
    void testFacingEquality() {
        Facing facing1 = Facing.of(1, 0);
        Facing facing2 = Facing.of(1, 0);
        Facing facing3 = Facing.of(0, 1);

        assertEquals(facing1, facing2);
        assertNotEquals(facing1, facing3);
    }

    @Test
    void testFacingHashCode() {
        Facing facing1 = Facing.of(1, 0);
        Facing facing2 = Facing.of(1, 0);

        assertEquals(facing1.hashCode(), facing2.hashCode());
    }

    @Test
    void testToString() {
        Facing facing = Facing.of(1, 0);
        assertEquals("Facing{direction=[1, 0]}", facing.toString());
    }
}