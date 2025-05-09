package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

public class PlayerStateND {
    private Cell position;
    private Facing facing;

    public PlayerStateND(@NotNull Cell startPosition, @NotNull Facing startFacing) {
        SimplePreconditions.checkArgument(startPosition.size() == startFacing.size(), "Position and Facing must be same size");
        this.position = startPosition;
        this.facing = startFacing;
    }

    public Cell getPosition() {
        return position;
    }

    public Facing getFacing() {
        return facing;
    }

    public void moveForward() {
        position = facing.moveForward(position);
    }

    public void turn(int dimA, int dimB) {
        facing = facing.turn(dimA, dimB);
    }

}