package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Генератор лабиринта на основе алгоритма Recursive Backtracker (Depth-First Search).
 *
 * <p>Класс предназначен для однократного использования: создаётся объект, вызывается метод
 * {@link #generateMaze()}, после чего объект не должен использоваться повторно.</p>
 *
 * <p>Выбор генератора случайных чисел (например, {@link java.util.Random}, {@link java.security.SecureRandom},
 * {@link java.util.concurrent.ThreadLocalRandom}) полностью лежит на вызывающей стороне.</p>
 */
public class RecursiveBacktracker {

    private final @NotNull Random random;
    private final @NotNull Set<Cell> visited = new HashSet<>();
    private final @NotNull Maze maze;
    private boolean generated = false;

    /**
     * Конструктор генератора лабиринта.
     *
     * @param mazeDim размерность лабиринта (должна быть непустой)
     * @param random  генератор случайных чисел, ответственность за корректность и потокобезопасность
     *                которого лежит на вызывающей стороне
     */
    public RecursiveBacktracker(@NotNull MazeDim mazeDim, @NotNull Random random) {
        if (mazeDim.size() == 0) {
            throw new IllegalArgumentException("Maze dimension must not be empty");
        }
        maze = MazeFactory.createNotConnectedMaze(mazeDim);
        this.random = random;
    }

    /**
     * Генерирует лабиринт и возвращает его.
     *
     * @return сгенерированный лабиринт
     * @throws IllegalStateException при повторном вызове метода
     */
    @NotNull
    public Maze generateMaze() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;

        final Cell startCell = maze.getRandomCell(random);
        generateMazeFrom(startCell);
        return maze;
    }

    private void generateMazeFrom(@NotNull Cell startCell) {
        final Deque<Cell> stack = new ArrayDeque<>();
        visited.add(startCell);
        stack.push(startCell);

        while (!stack.isEmpty()) {
            final Cell currentCell = stack.peek();

            final List<Cell> unvisitedNeighbors = new ArrayList<>(getUnvisitedNeighbors(currentCell));

            if (!unvisitedNeighbors.isEmpty()) {
                // сразу берем случайный элемент
                final Cell neighbor = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                maze.addPass(currentCell, Set.of(neighbor));
                visited.add(neighbor);
                stack.push(neighbor);
            } else {
                stack.pop();
            }
        }
    }

    private List<Cell> getUnvisitedNeighbors(@NotNull Cell cell) {
        return maze.getAllNeighbors(cell).filter(c -> !visited.contains(c)).toList();
    }
}
