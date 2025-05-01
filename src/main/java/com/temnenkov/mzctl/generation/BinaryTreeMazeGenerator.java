package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Генератор лабиринта с использованием алгоритма Binary Tree.
 * Алгоритм очень прост: для каждой ячейки выбирает случайного соседа
 * в положительном направлении и делает проход.
 */
public class BinaryTreeMazeGenerator implements MazeGenerator  {
    private final MazeDim mazeDim;
    private final Random random;

    public BinaryTreeMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.mazeDim = mazeDim;
        this.random = random;
    }

    @Override
    public Maze generateMaze() {
       final Maze maze = MazeFactory.createNotConnectedMaze(mazeDim);

        for (Cell cell : maze) {
            final List<Cell> neighbors = new ArrayList<>();

            // проходим по измерениям и добавляем соседей в "положительном" направлении
            for (int dim = 0; dim < mazeDim.size(); dim++) {
                final Cell neighbor = cell.plusOne(dim);
                if (maze.isValid(neighbor, dim)) {
                    neighbors.add(neighbor);
                }
            }

            if (!neighbors.isEmpty()) {
                final Cell chosenNeighbor = neighbors.get(random.nextInt(neighbors.size()));
                maze.addPass(cell, chosenNeighbor);
            }
        }

        return maze;
    }
}
