package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

public class FixedPlayerPositionProvider implements PlayerPositionProvider {

    private final int row;
    private final int column;
    @NotNull
    private final Facing facing;

    public FixedPlayerPositionProvider(int row, int column, @NotNull Facing facing) {
        this.row = row;
        this.column = column;
        this.facing = facing;
    }

    @Override
    public @NotNull PlayerStateND createPlayerPosition(@NotNull Maze maze) {
        final Cell cell = Cell.ofRowAndColumn(row, column);
        SimplePreconditions.checkState(maze.isValid(cell), "Cell " + cell + " is not valid");
        return new PlayerStateND(cell, facing);
    }
}
