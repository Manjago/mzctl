package com.temnenkov.mzctl.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Многомерный лабиринт
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Maze implements Iterable<Cell> {
    private final @NotNull MazeDim mazeDimension;
    private final @NotNull Map<Cell, Set<Cell>> passes;
    private final int totalCellCount;

    /**
     * Конструктор для сериализации jackson
     *
     * @param mazeDimension размерность
     * @param passes        допустимые переходы из комнаты в комнату
     */
    @JsonCreator
    Maze(@JsonProperty("mazeDimension") @NotNull MazeDim mazeDimension,
            @JsonProperty("passes") @NotNull Map<Cell, Set<Cell>> passes) {
        this.mazeDimension = mazeDimension;
        this.passes = new HashMap<>();
        passes.forEach((key, value) -> this.passes.put(key, new HashSet<>(value)));
        this.totalCellCount = calculateTotalCellCount();
    }

    /**
     * Конструктор для создания лабиринта
     *
     * @param mazeDimension список измерений
     */
    Maze(@NotNull MazeDim mazeDimension) {
        this.mazeDimension = mazeDimension;
        this.passes = new HashMap<>();
        this.totalCellCount = calculateTotalCellCount();
    }

    /**
     * Получить размерность лабиринта
     *
     * @return размерность лабиринта
     */
    public @NotNull MazeDim getMazeDimension() {
        return mazeDimension;
    }

    @Transient
    public MazeLinker getLinker() {
        return new MazeLinker();
    }

    public class MazeLinker {
        public MazeLinker link(@NotNull Cell from, @NotNull Cell to) {
            addPass(from ,Set.of(to));
            return this;
        }
    }

    /**
     * Добавить двусторонний проход из комнаты в комнаты
     *
     * @param cell      комната откуда
     * @param neighbors комнаты куда
     */
    public void addPass(Cell cell, Set<Cell> neighbors) {
        validateCell(cell);
        neighbors.forEach(this::validateCell);

        passes.computeIfAbsent(cell, k -> new HashSet<>()).addAll(neighbors);
        neighbors.forEach(neighbor -> passes.computeIfAbsent(neighbor, k -> new HashSet<>()).add(cell));
    }

    private void validateCell(@NotNull Cell cell) {
        if (cell.coordinates().size() != mazeDimension.size()) {
            throw new IllegalArgumentException("Cell dimension mismatch");
        }
    }

    /**
     * Получить комнаты, в которые можно попасть из текущей комнаты
     *
     * @param cell текущая комната
     * @return набор комнат, в которые можно попасть
     */
    public Set<Cell> getAvailableNeighbors(Cell cell) {
        return Set.copyOf(passes.getOrDefault(cell, Set.of()));
    }

    /**
     * Получить все соседние клетки (с границами-стенами или без) для текущей комнаты cell
     *
     * @param cell текущая комната
     * @return все соседние клетки
     */
    public Stream<Cell> getAllNeighbors(Cell cell) {
        return IntStream.range(0, mazeDimension.size()).boxed().flatMap(i -> Stream.of(cell.minusOne(i),
                cell.plusOne(i)).filter(pretender -> isValid(pretender, i)));
    }

    /**
     * Общее количество комнат в лабиринте
     * @return число - общее количество комнат в лабиринте
     */
    public int totalCellCount() {
        return totalCellCount;
    }

    private int calculateTotalCellCount() {
        int result = 1;
        for (int i = 0; i < mazeDimension.size(); i++) {
            result *= mazeDimension.dimSize(i);
        }
        return result;
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
        return coord >= 0 && coord < mazeDimension.dimSize(dimension);
    }

    /**
     * Можно ли пройти из комнаты from в комнату to (то есть между ними нет стены)
     *
     * @param from откуда идем
     * @param to   куда хотим попасть
     * @return true, если пройти можно (нет стены), false в противном случае
     */
    public boolean canPass(Cell from, Cell to) {
        return passes.getOrDefault(from, Set.of()).contains(to);
    }

    /**
     * Получить случайную комнату в пределах лабиринта.
     *
     * <p>Ответственность за корректную работу генератора случайных чисел (например,
     * потокобезопасность, криптографическая стойкость и т.д.) лежит на вызывающей стороне.
     * Если метод вызывается из нескольких потоков, рекомендуется использовать
     * {@link java.util.concurrent.ThreadLocalRandom#current()} или другой потокобезопасный
     * генератор.</p>
     *
     * @param random интерфейс Random для генерации случайных чисел
     * @return случайная комната в пределах лабиринта
     */
    public Cell getRandomCell(@NotNull Random random) {
        final List<Integer> coords = new ArrayList<>();
        for (int i = 0; i < mazeDimension.size(); ++i) {
            coords.add(random.nextInt(mazeDimension.dimSize(i)));
        }
        return new Cell(coords);
    }

    public Stream<Cell> stream() {
        return StreamSupport.stream(
                Spliterators.spliterator(
                        iterator(),
                        totalCellCount(),
                        Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED
                ),
                false
        );
    }

    @Override
    public @NotNull Iterator<Cell> iterator() {
        return new MazeCellIterator();
    }

    private class MazeCellIterator implements Iterator<Cell> {

        private boolean hasNext = true;
        private final int[] currentCoords;

        private MazeCellIterator() {
            this.currentCoords = new int[mazeDimension.size()];
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
            for (int dim = mazeDimension.size() - 1; dim >= 0; dim--) {
                currentCoords[dim]++;
                if (currentCoords[dim] < mazeDimension.dimSize(dim)) {
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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        Maze maze = (Maze) o;
        return mazeDimension.equals(maze.mazeDimension) && passes.equals(maze.passes);
    }

    @Override
    public int hashCode() {
        int result = mazeDimension.hashCode();
        result = 31 * result + passes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Maze{" + "mazeDimension=" + mazeDimension + ", passes=" + passes + '}';
    }
}
