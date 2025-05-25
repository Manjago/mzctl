package com.temnenkov.mzctl.game.model;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

/**
 * Короткое (однострочное) описание окружения для тестов.
 * Использует символы: '.' - проход, '#' - стена, 'X' - граница лабиринта
 */
public class ShortEnvironmentDescriber implements EnvironmentDescriber {
    private final Maze maze;

    public ShortEnvironmentDescriber(@NotNull Maze maze) {
        this.maze = maze;
    }

    @Override
    public @NotNull String describeEnvironment(@NotNull PlayerStateND player) {
        return String.format("F:%s L:%s R:%s B:%s",
                describeDirection(player, player.getFacing()),
                describeDirection(player, player.getFacing().rotateCounterClockwise2D()),
                describeDirection(player, player.getFacing().rotateClockwise2D()),
                describeDirection(player, player.getFacing().opposite()));
    }

    private @NotNull String describeDirection(@NotNull PlayerStateND player, @NotNull Facing facing) {
        final Cell fromCell = player.getPosition();
        final Cell toCell = facing.moveForward(fromCell);

        if (!maze.isValid(toCell)) {
            return "X"; // граница лабиринта
        }
        if (maze.canPass(fromCell, toCell)) {
            return "."; // проход
        }
        return "#"; // стена
    }
}