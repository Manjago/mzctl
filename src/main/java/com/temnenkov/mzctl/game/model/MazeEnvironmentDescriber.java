package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Генерирует текстовое описание окружения игрока в двумерном лабиринте.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MazeEnvironmentDescriber implements EnvironmentDescriber {

    private final Maze maze;

    /**
     * Создаёт описатель окружения для заданного двумерного лабиринта.
     *
     * @param maze лабиринт для описания
     * @throws IllegalArgumentException если лабиринт не является двумерным
     */
    @JsonCreator
    public MazeEnvironmentDescriber(@JsonProperty("maze") @NotNull Maze maze) {
        SimplePreconditions.checkArgument(maze.getMazeDimension().size() == 2, "Maze must be 2D");
        this.maze = maze;
    }

    /**
     * Возвращает текстовое описание окружения игрока.
     *
     * @param player текущее состояние игрока
     * @return текстовое описание окружения
     */
    @Override
    public @NotNull String describeEnvironment(@NotNull PlayerStateND player) {
        final StringBuilder description = new StringBuilder("Вы находитесь в комнате.\n");

        description.append("- Впереди: ")
                .append(describeDirection(player, player.getFacing()))
                .append("\n");

        description.append("- Слева: ")
                .append(describeDirection(player, player.getFacing().rotateCounterClockwise2D()))
                .append("\n");

        description.append("- Справа: ")
                .append(describeDirection(player, player.getFacing().rotateClockwise2D()))
                .append("\n");

        description.append("- Сзади: ")
                .append(describeDirection(player, player.getFacing().opposite()))
                .append("\n");

        return description.toString();
    }

    /**
     * Описывает, что находится в заданном направлении от игрока.
     *
     * @param player состояние игрока
     * @param facing направление для описания
     * @return строковое описание объекта в указанном направлении
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        MazeEnvironmentDescriber that = (MazeEnvironmentDescriber) o;
        return maze.equals(that.maze);
    }

    @Override
    public int hashCode() {
        return maze.hashCode();
    }

    @Override
    public String toString() {
        return "MazeEnvironmentDescriber{maze=" + maze + '}';
    }
}