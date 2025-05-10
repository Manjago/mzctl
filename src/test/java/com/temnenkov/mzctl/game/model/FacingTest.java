package com.temnenkov.mzctl.game.model;

import com.temnenkov.mzctl.model.Cell;
import org.junit.jupiter.api.Test;

import static com.temnenkov.mzctl.game.model.Facing.AxisDirection;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacingTest {

    @Test
    void testCreateFacing() {
        // здесь не используем константы - хочется проверить создание
        Facing facing = Facing.of(AxisDirection.POSITIVE, AxisDirection.NEGATIVE);
        assertArrayEquals(new AxisDirection[]{AxisDirection.POSITIVE, AxisDirection.NEGATIVE}, facing.getDirections());
    }

    @Test
    void testCreateFacingWithZeroVector() {
        assertThrows(IllegalArgumentException.class, () -> Facing.of(AxisDirection.ZERO, AxisDirection.ZERO));
    }

    @Test
    void testMoveForward() {
        Cell initialCell = Cell.of(2, 3);
        Cell resultCell = Facing.SOUTH.moveForward(initialCell);
        assertEquals(Cell.of(2, 4), resultCell);
    }

    // South → East → North → West → South
    @Test
    void testRotateCounterClockwise2DFull() {
        Facing facing = Facing.SOUTH;

        Facing turnedFacing = facing.rotateCounterClockwise2D();
        assertEquals(Facing.EAST, turnedFacing);

        turnedFacing = turnedFacing.rotateCounterClockwise2D();
        assertEquals(Facing.NORTH, turnedFacing);

        turnedFacing = turnedFacing.rotateCounterClockwise2D();
        assertEquals(Facing.WEST, turnedFacing);

        turnedFacing = turnedFacing.rotateCounterClockwise2D();
        assertEquals(Facing.SOUTH, turnedFacing);
    }

    // South → West → North → East → South
    @Test
    void testRotateClockwise2DFull() {
        Facing facing = Facing.SOUTH;

        Facing turnedFacing = facing.rotateClockwise2D();
        assertEquals(Facing.WEST, turnedFacing);

        turnedFacing = turnedFacing.rotateClockwise2D();
        assertEquals(Facing.NORTH, turnedFacing);

        turnedFacing = turnedFacing.rotateClockwise2D();
        assertEquals(Facing.EAST, turnedFacing);

        turnedFacing = turnedFacing.rotateClockwise2D();
        assertEquals(Facing.SOUTH, turnedFacing);
    }

    @Test
    void testFacingEquality() {
        Facing facing1 = Facing.of(AxisDirection.POSITIVE, AxisDirection.NEGATIVE);
        Facing facing2 = Facing.of(AxisDirection.POSITIVE, AxisDirection.NEGATIVE);
        Facing facing3 = Facing.WEST;

        assertEquals(facing1, facing2);
        assertNotEquals(facing1, facing3);
    }

    @Test
    void testFacingHashCode() {
        Facing facing1 = Facing.NORTH;
        Facing facing2 = Facing.NORTH;

        assertEquals(facing1.hashCode(), facing2.hashCode());
    }

    @Test
    void testToString() {
        Facing facing = Facing.NORTH;
        assertEquals("Facing[Y(-)]", facing.toString());
    }

    @Test
    void testFacingEqualsWithNullAndDifferentClass() {
        Facing facing = Facing.NORTH;

        assertNotEquals(null, facing);
        // именно в таком порядке, чтобы проверить equals именно у Facing
        assertNotEquals(facing, "Some String");
    }

    @Test
    void testOpposite() {
        assertEquals(Facing.WEST, Facing.EAST.opposite());
        assertEquals(Facing.EAST, Facing.WEST.opposite());
        assertEquals(Facing.NORTH, Facing.SOUTH.opposite());
        assertEquals(Facing.SOUTH, Facing.NORTH.opposite());
        assertEquals(Facing.SOUTH, Facing.SOUTH.opposite().opposite());
    }

    @Test
    void testInvalidFacingCreationEmpty() {
        assertThrows(IllegalArgumentException.class, Facing::of);
    }

    @Test
    void testInvalidFacingCreationZeroOnly() {
        assertThrows(IllegalArgumentException.class, () -> Facing.of(AxisDirection.ZERO, AxisDirection.ZERO));
    }

    @Test
    void testRotateCounterClockwise2D() {
        final Facing facing = Facing.EAST;
        assertEquals(facing.rotateCounterClockwise2D(), facing.turn(Facing.Dimension.Y, Facing.Dimension.X));
    }

    @Test
    void testRotateClockwise2D() {
        final Facing facing = Facing.EAST;
        assertEquals(facing.rotateClockwise2D(), facing.turn(Facing.Dimension.X, Facing.Dimension.Y));
    }

    @Test
    void testRotateCounterClockwiseInvalidDimension() {
        final Facing facing = Facing.of(AxisDirection.POSITIVE);
        assertThrows(IllegalStateException.class, facing::rotateCounterClockwise2D);
    }

    @Test
    void testRotateClockwiseInvalidDimension() {
        final Facing facing = Facing.of(AxisDirection.NEGATIVE);
        assertThrows(IllegalStateException.class, facing::rotateClockwise2D);
    }

    @Test
    void testInvalidSizeMoveForward() {
        Cell initialCell = Cell.of(1);
        assertThrows(IllegalStateException.class, () -> Facing.SOUTH.moveForward(initialCell));
    }
}