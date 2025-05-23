package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.util.RandomProvider;
import org.jetbrains.annotations.NotNull;

public class RandomPlayerPositionProvider implements PlayerPositionProvider {

    @NotNull
    private final RandomProvider randomProvider;

    public RandomPlayerPositionProvider(@NotNull RandomProvider randomProvider) {
        this.randomProvider = randomProvider;
    }

    @Override
    public @NotNull PlayerStateND createPlayerPosition(@NotNull Maze maze) {
        final Cell randomCell = maze.getRandomCell(randomProvider.getRandom());
        final Facing randomFacing = Facing.randomFacing(randomProvider.getRandom());
        return new PlayerStateND(randomCell, randomFacing);
    }
}
