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
        Facing facing = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.POSITIVE);
        assertArrayEquals(new Facing.AxisDirection[]{Facing.AxisDirection.ZERO, Facing.AxisDirection.POSITIVE}, facing.getDirections());
    }

    @Test
    void testCreateFacingWithZeroVector() {
        assertThrows(IllegalArgumentException.class, () -> Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.ZERO));
    }

    @Test
    void testMoveForward() {
        Facing facing = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.POSITIVE);
        Cell initialCell = Cell.of(2, 3);
        Cell resultCell = facing.moveForward(initialCell);
        assertEquals(Cell.of(2, 4), resultCell);
    }

    @Test
    void testTurn() {
        // Начальное направление (смотрим вправо)
        Facing facing = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.POSITIVE);

        // Поворот на 90 градусов (в плоскости 0-1)
        Facing turnedFacing = facing.turn(Facing.Dimension.X, Facing.Dimension.Y);
        assertArrayEquals(new Facing.AxisDirection[]{Facing.AxisDirection.NEGATIVE, Facing.AxisDirection.ZERO}, turnedFacing.getDirections());

        // Еще один поворот на 90 градусов
        Facing turnedAgainFacing = turnedFacing.turn(Facing.Dimension.X, Facing.Dimension.Y);
        assertArrayEquals(new Facing.AxisDirection[]{Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE}, turnedAgainFacing.getDirections());
    }

    @Test
    void testFacingEquality() {
        Facing facing1 = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE);
        Facing facing2 = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE);
        Facing facing3 = Facing.of(Facing.AxisDirection.NEGATIVE, Facing.AxisDirection.ZERO);

        assertEquals(facing1, facing2);
        assertNotEquals(facing1, facing3);
    }

    @Test
    void testFacingHashCode() {
        Facing facing1 = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE);
        Facing facing2 = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE);

        assertEquals(facing1.hashCode(), facing2.hashCode());
    }

    @Test
    void testToString() {
        Facing facing = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE);
        assertEquals("Facing[Y(-)]", facing.toString());
    }

    @Test
    void testCreateFacingWithEmptyArray() {
        assertThrows(IllegalArgumentException.class, Facing::of);
    }

    @Test
    void testFacingEqualsWithNullAndDifferentClass() {
        Facing facing = Facing.of(Facing.AxisDirection.ZERO, Facing.AxisDirection.NEGATIVE);

        assertNotEquals(null, facing);
        // именно в таком порядке, чтобы проверить equals именно у Facing
        assertNotEquals(facing, "Some String");
    }
}