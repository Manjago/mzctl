package com.temnenkov.mzctl.model;

import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CellularAutomataMazeImpl implements CellularAutomataMaze {

    private static final String CTOR = ".ctor";
    @NotNull
    private final MazeDim mazeDim;
    @NotNull
    private final Random random;
    private final Set<Cell> walls = new HashSet<>();
    private final int totalCellCount;

    public CellularAutomataMazeImpl(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.mazeDim = SimplePreconditions.checkNotNull(mazeDim, "mazeDim", CTOR);
        this.random = SimplePreconditions.checkNotNull(random, "random", CTOR);
        this.totalCellCount = calculateTotalCellCount();
    }

    @Override
    public MazeDim getDimensions() {
        return mazeDim;
    }

    @Override
    public boolean isWall(int... coords) {
        return isWall(Cell.of(coords));
    }

    @Override
    public boolean isWall(Cell cell) {
        return walls.contains(cell);
    }

    @Override
    public void setWall(boolean isWall, int... coords) {
        setWall(isWall, Cell.of(coords));
    }

    @Override
    public void setWall(boolean isWall, Cell cell) {
        if (isWall) {
            walls.add(cell);
        } else {
            walls.remove(cell);
        }
    }

    @Override
    public int countWallNeighbors(int... coords) {
        return countWallNeighbors(Cell.of(coords));
    }

    @Override
    public int countWallNeighbors(Cell cell) {
        return Math.toIntExact(getAllNeighbors(cell).filter(this::isWall).count());
    }

    @Override
    public CellularAutomataMaze initialize(double fillProbability) {
        stream().forEach(cell -> setWall(random.nextDouble() <= fillProbability, cell));
        return this;
    }

    @Override
    public @NotNull Iterator<Cell> iterator() {
        return new MazeCellIterator();
    }

    @Override
    public Stream<Cell> stream() {
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iterator(),
                        totalCellCount,
                        Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED
                ),
                false
        );
    }

    private class MazeCellIterator implements Iterator<Cell> {

        private boolean hasNext = true;
        private final int[] currentCoords;

        private MazeCellIterator() {
            this.currentCoords = new int[mazeDim.size()];
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public @NotNull Cell next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }

            final Cell currentCell = Cell.of(currentCoords);

            advanceCoordinates();

            return currentCell;
        }

        private void advanceCoordinates() {
            for (int dim = mazeDim.size() - 1; dim >= 0; dim--) {
                currentCoords[dim]++;
                if (currentCoords[dim] < mazeDim.dimSize(dim)) {
                    // нет переполнения, можно остановиться
                    return;
                } else {
                    // переполнение, сбрасываем текущую координату и переходим на следующий разряд
                    currentCoords[dim] = 0;
                }
            }
            // если мы дошли сюда, значит, прошли все координаты
            hasNext = false;
        }
    }

    /**
     * Не вышла ли комната cell за пределы лабиринта по измерению dimension
     *
     * @param cell      комната
     * @param dimension измерение
     * @return true, если комната в пределах лабиринта, false - в противном случае
     */
    private boolean isValid(@NotNull Cell cell, int dimension) {
        final int coord = cell.coord(dimension);
        return coord >= 0 && coord < mazeDim.dimSize(dimension);
    }

    /**
     * Не вышла ли комната cell за пределы лабиринта
     *
     * @param cell      комната
     * @return true, если комната в пределах лабиринта, false - в противном случае
     */
    private boolean isValid(@NotNull Cell cell) {
        for(int i=0; i< mazeDim.size(); i++) {
            if (!isValid(cell, i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Получить все соседние клетки (с границами-стенами или без) для текущей комнаты cell
     *
     * @param cell текущая комната
     * @return все соседние клетки
     */
    private Stream<Cell> getAllNeighbors(Cell cell) {
        return IntStream.range(0, mazeDim.size()).boxed().flatMap(dimNum ->
                Stream.of(
                        cell.minusOne(dimNum),
                        cell.plusOne(dimNum)
                ).filter(pretender -> isValid(pretender, dimNum)));
    }

    private int calculateTotalCellCount() {
        int result = 1;
        for (int i = 0; i < mazeDim.size(); i++) {
            result *= mazeDim.dimSize(i);
        }
        return result;
    }

}
