package com.temnenkov.mzctl.agent;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MazeExplorer {
    private final Maze maze;
    private final Random random;

    public MazeExplorer(Maze maze, Random random) {
        this.maze = maze;
        this.random = random;
    }

    /**
     * Проверка, что лабиринт connected
     *
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected() {
        return isConnected(maze.getRandomCell(random));
    }

    /**
     * Проверка, что лабиринт connected
     *
     * @param startCell комната, с которой начинаем
     * @return true если connected, false - в противном случае
     */
    public boolean isConnected(@NotNull Cell startCell) {
        final Set<Cell> visited = new HashSet<>();

        dfs(startCell, visited);

        return visited.size() == maze.totalCellCount();
    }

    /**
     * Проверка, что лабиринт не содержит циклов
     * @return true, если лабиринт не содержит циклов, false в противном случае
     */
    public boolean isAcyclic() {
        final Set<Cell> visited = new HashSet<>();
        for (Cell cell : maze) {
            if (!visited.contains(cell) && hasCycle(cell, visited)) {
                return false; // если нашли цикл, сразу возвращаем false
            }
        }
        return true; // циклов нет, возвращаем true
    }

    private static class CellWithParent {
        private final Cell current;
        private final Cell parent;
        private final Set<Cell> visited;

        private CellWithParent(Cell current, Cell parent, Set<Cell> visited) {
            this.current = current;
            this.parent = parent;
            this.visited = visited;
        }

        @Contract(value = "_ -> new", pure = true)
        private @NotNull CellWithParent advance(@NotNull Cell next) {
            return new CellWithParent(next, current, visited);
        }

        private void markCurrentAsVisited() {
            visited.add(current);
        }
    }

    private boolean hasCycle(@NotNull Cell cell, @NotNull Set<Cell> visited) {

        final Deque<CellWithParent> stack = new ArrayDeque<>();
        stack.push(new CellWithParent(cell, null, visited));

        while (!stack.isEmpty()) {
            final CellWithParent cellWithParent = stack.pop();
            cellWithParent.markCurrentAsVisited();

            for (Cell neighbor : maze.getAvailableNeighbors(cellWithParent.current)) {
                if (!cellWithParent.visited.contains(neighbor)) {
                    stack.push(cellWithParent.advance(neighbor));
                } else if (!neighbor.equals(cellWithParent.parent)) {
                    // если сосед уже посещён и это не родитель, значит, нашли цикл
                    return true;
                }
            }
        }
        return false; // циклов не нашли
    }

    /**
     * Проверка, что лабиринт perfect (connected + acyclic)
     * @return true, если лабиринт perfect, false в противном случае
     */
    public boolean isPerfect() {
        return isConnected() && isAcyclic();
    }

    public long deadEndCount() {
        return maze.stream()
                .filter(c -> maze.getAvailableNeighbors(c).size() == 1)
                .count();
    }

    private void dfs(@NotNull Cell current, @NotNull Set<Cell> visited) {
        visited.add(current);

        for (Cell neighbor : maze.getAvailableNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }
}
