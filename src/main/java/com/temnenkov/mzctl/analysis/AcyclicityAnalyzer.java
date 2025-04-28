package com.temnenkov.mzctl.analysis;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class AcyclicityAnalyzer {
    private final Maze maze;

    public AcyclicityAnalyzer(Maze maze) {
        this.maze = maze;
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

}
