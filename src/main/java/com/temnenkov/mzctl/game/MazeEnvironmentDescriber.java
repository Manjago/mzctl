package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MazeEnvironmentDescriber {

    private final Maze maze;

    public MazeEnvironmentDescriber(Maze maze) {
        this.maze = maze;
    }

    /**
     * Возвращает текстовое описание окружения игрока.
     *
     * @param player текущее состояние игрока
     * @return текстовое описание окружения
     */
    public String describeEnvironment(PlayerStateND player) {
        StringBuilder description = new StringBuilder();
        description.append("Вы находитесь в комнате.\n");

        description.append("- Впереди: ").append(describeDirection(player, player.getFacing())).append("\n");
        description.append("- Слева: ").append(describeDirection(player, turnLeft(player.getFacing()))).append("\n");
        description.append("- Справа: ").append(describeDirection(player, turnRight(player.getFacing()))).append("\n");

        return description.toString();
    }

    /**
     * Описывает, что находится в направлении взгляда.
     */
    private @NotNull String describeDirection(@NotNull PlayerStateND player, @NotNull Facing facing) {
        final Cell fromCell = player.getPosition();
        final Cell toCell = facing.moveForward(fromCell);

        if (!maze.isValid(toCell)) {
            return "граница лабиринта";
        }
        if (maze.canPass(fromCell, toCell)) {
            return "проход";
        }
        return "стена";
    }

    /**
     * Поворот налево в двумерном лабиринте.
     */
    @Contract("_ -> new")
    private @NotNull Facing turnLeft(@NotNull Facing facing) {
        return facing.rotateCounterClockwise2D(); // для 2D лабиринта
    }

    /**
     * Поворот направо в двумерном лабиринте.
     */
    @Contract("_ -> new")
    private @NotNull Facing turnRight(@NotNull Facing facing) {
        return facing.rotateClockwise2D(); // для 2D лабиринта
    }
}