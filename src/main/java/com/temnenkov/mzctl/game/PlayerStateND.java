package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.model.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PlayerStateND {
    private Cell position;                 // текущая позиция игрока
    private int[] direction;               // текущее направление взгляда (вектор)

    /**
     * Создаёт новое состояние игрока.
     *
     * @param startPosition начальная позиция игрока
     * @param startDirection начальное направление взгляда (вектор)
     */
    public PlayerStateND(@NotNull Cell startPosition, int @NotNull [] startDirection) {
        if (startPosition.size() != startDirection.length) {
            throw new IllegalArgumentException("Position and direction must have the same dimensions.");
        }
        if (Arrays.stream(startDirection).allMatch(d -> d == 0)) {
            throw new IllegalArgumentException("Direction vector cannot be zero.");
        }
        this.position = startPosition;
        this.direction = startDirection.clone();
    }

    /**
     * Возвращает текущую позицию игрока.
     *
     * @return позиция (Cell)
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Возвращает текущее направление взгляда игрока.
     *
     * @return направление (вектор)
     */
    public int[] getDirection() {
        return direction.clone();
    }

    /**
     * Двигает игрока вперёд в текущем направлении взгляда.
     */
    public void moveForward() {
        int[] newCoords = addVectors(position.getCoordinates(), direction);
        position = Cell.of(newCoords);
    }

    /**
     * Поворачивает игрока в плоскости, заданной двумя измерениями.
     *
     * @param dimA первое измерение плоскости
     * @param dimB второе измерение плоскости
     */
    public void turn(int dimA, int dimB) {
        if (dimA == dimB) {
            throw new IllegalArgumentException("Dimensions must be different.");
        }
        int temp = direction[dimA];
        direction[dimA] = -direction[dimB];
        direction[dimB] = temp;
    }

    /**
     * Складывает два вектора.
     *
     * @param a первый вектор
     * @param b второй вектор
     * @return сумма векторов
     */
    private int[] addVectors(int[] a, int[] b) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    @Override
    public String toString() {
        return "PlayerStateND{" +
                "position=" + position +
                ", direction=" + Arrays.toString(direction) +
                '}';
    }
}