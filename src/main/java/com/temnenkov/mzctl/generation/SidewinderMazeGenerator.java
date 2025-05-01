package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Многомерная реализация алгоритма Sidewinder без рекурсии.
 */
public class SidewinderMazeGenerator implements MazeGenerator {
    private final MazeDim mazeDim;
    private final Random random;

    private static final int MAIN_DIM = 0;  // аналогично движению вправо (x)
    private static final int ADDITIONAL_DIM = 1;  // аналогично движению вверх (y)

    public SidewinderMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        if (mazeDim.size() < 2) {
            throw new UnsupportedOperationException("Sidewinder algorithm requires at least two dimensions");
        }
        this.mazeDim = mazeDim;
        this.random = random;
    }

    @Override
    public Maze generateMaze() {
        final Maze maze = MazeFactory.createNotConnectedMaze(mazeDim);

        final int[] dimensionLengths = mazeDim.dimensions().stream().mapToInt(Integer::intValue).toArray();
        final int totalCells = getTotalCells(dimensionLengths);

        for (int index = 0; index < totalCells; index++) {
            final int[] baseCoordinates = calculateBaseCoordinates(index, dimensionLengths);
            carveSidewinderLine(maze, baseCoordinates, dimensionLengths);
        }

        return maze;
    }

    private void carveSidewinderLine(Maze maze, int[] baseCoordinates, int @NotNull [] dimensionLengths) {
        final int lengthMain = dimensionLengths[MAIN_DIM];
        final List<Cell> runSet = new ArrayList<>();

        for (int posMain = 0; posMain < lengthMain; posMain++) {
            baseCoordinates[MAIN_DIM] = posMain;
            final Cell current = new Cell(baseCoordinates.clone());
            runSet.add(current);

            final boolean carveForward = posMain < lengthMain - 1 && random.nextBoolean();

            if (carveForward) {
                final Cell neighbor = current.plusOne(MAIN_DIM);
                maze.addPass(current, neighbor);
            } else {
                boolean connected = false;
                // Перебираем все дополнительные измерения и гарантированно соединяем с предыдущими слоями
                for (int dim = 1; dim < dimensionLengths.length; dim++) {
                    if (baseCoordinates[dim] > 0) {
                        final Cell chosenCell = runSet.get(random.nextInt(runSet.size()));
                        final Cell neighbor = chosenCell.minusOne(dim);
                        maze.addPass(chosenCell, neighbor);
                        connected = true;
                        break; // соединяем хотя бы по одному измерению и выходим
                    }
                }
                if (!connected && posMain < lengthMain - 1) {
                    // если нет предыдущих слоев, соединяем вперед по основному измерению
                    final Cell neighbor = current.plusOne(MAIN_DIM);
                    maze.addPass(current, neighbor);
                }
                runSet.clear();
            }
        }
    }

    /**
     * Вычисляет общее количество "линий" (групп ячеек), которые нужно обработать алгоритмом Sidewinder.
     */
    @Contract(pure = true)
    private int getTotalCells(int @NotNull [] dimensionLengths) {
        int total = 1;
        for (int i = 0; i < dimensionLengths.length; i++) {
            if (i != MAIN_DIM && i != ADDITIONAL_DIM) {
                total *= dimensionLengths[i];
            }
        }
        return total * dimensionLengths[ADDITIONAL_DIM];
    }

    /**
     * Преобразует индекс линии в многомерные координаты ячейки, с которой начинается обработка.
     */
    @Contract(pure = true)
    private int @NotNull [] calculateBaseCoordinates(int index, int @NotNull [] dimensionLengths) {
        final int[] coords = new int[dimensionLengths.length];
        int remainder = index;

        for (int dim = dimensionLengths.length - 1; dim >= 0; dim--) {
            if (dim == MAIN_DIM) {
                coords[dim] = 0; // начальная позиция по главному измерению всегда 0
                continue;
            }
            final int length = dimensionLengths[dim];
            coords[dim] = remainder % length;
            remainder /= length;
        }
        return coords;
    }
}