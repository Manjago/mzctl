package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Класс, описывающий состояние игрока в N-мерном лабиринте.
 * Хранит текущую позицию и направление взгляда игрока.
 *
 * <p>Класс является мутабельным: методы перемещения и поворота изменяют внутреннее состояние объекта.</p>
 */
public class PlayerStateND {
    private @NotNull Cell position;
    private @NotNull Facing facing;

    /**
     * Создаёт новое состояние игрока с заданной начальной позицией и направлением взгляда.
     *
     * @param startPosition начальная позиция игрока
     * @param startFacing начальное направление взгляда игрока
     * @throws IllegalArgumentException если размерность позиции и направления не совпадают
     */
    @JsonCreator
    public PlayerStateND(@JsonProperty("position") @NotNull Cell startPosition, @JsonProperty("facing") @NotNull Facing startFacing) {
        SimplePreconditions.checkArgument(startPosition.size() == startFacing.size(),
                "Position and Facing must be same size");
        this.position = startPosition;
        this.facing = startFacing;
    }

    /**
     * Возвращает текущую позицию игрока.
     *
     * @return текущая позиция игрока
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Возвращает текущее направление взгляда игрока.
     *
     * @return текущее направление взгляда
     */
    public Facing getFacing() {
        return facing;
    }

    /**
     * Перемещает игрока на одну клетку вперед в текущем направлении взгляда.
     */
    public void moveForward() {
        position = predictMoveForward();
    }

    /**
     * Предсказывает позицию игрока на одну клетку вперед в текущем направлении взгляда.
     */
    public Cell predictMoveForward() {
        return facing.moveForward(position);
    }

    public boolean canMoveForward(@NotNull Maze maze) {
        final Cell nextPosition = predictMoveForward();
        return maze.canPass(this.position, nextPosition);
    }

    /**
     * Поворачивает игрока в плоскости, заданной двумя измерениями.
     *
     * @param dimA первое измерение плоскости
     * @param dimB второе измерение плоскости
     * @throws IllegalArgumentException если указаны некорректные измерения
     */
    public void turn(Facing.Dimension dimA, Facing.Dimension dimB) {
        facing = facing.turn(dimA, dimB);
    }

    /**
     * Поворачивает направление взгляда игрока по часовой стрелке (только для двумерного лабиринта).
     *
     * @throws IllegalStateException если лабиринт не двумерный
     */
    public void rotateClockwise2D() {
        facing = facing.rotateClockwise2D();
    }

    /**
     * Поворачивает направление взгляда игрока против часовой стрелки (только для двумерного лабиринта).
     *
     * @throws IllegalStateException если лабиринт не двумерный
     */
    public void rotateCounterClockwise2D() {
        facing = facing.rotateCounterClockwise2D();
    }

    /**
     * Меняет направление взгляда на противоположное.
     */
    public void opposite() {
        facing = facing.opposite();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        PlayerStateND that = (PlayerStateND) o;
        return position.equals(that.position) && facing.equals(that.facing);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + facing.hashCode();
        return result;
    }

    /**
     * Возвращает строковое представление состояния игрока.
     *
     * @return строковое представление текущего состояния
     */
    @Override
    public String toString() {
        return "PlayerStateND{position=" + position + ", facing=" + facing + '}';
    }
}