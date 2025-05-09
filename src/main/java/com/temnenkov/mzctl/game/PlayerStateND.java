package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.model.Cell;

public class PlayerStateND {
    private Cell position;
    private Facing facing;

    public PlayerStateND(Cell startPosition, Facing startFacing) {
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
        int[] dir = facing.getDirections();
        int temp = dir[dimA];
        dir[dimA] = -dir[dimB];
        dir[dimB] = temp;
        facing = new Facing(dir);
    }

}