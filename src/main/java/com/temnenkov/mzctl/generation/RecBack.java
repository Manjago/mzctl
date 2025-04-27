package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RecBack {

    private final @NotNull Random random = new SecureRandom();
    private final @NotNull Set<Cell> visited = new HashSet<>();
    private final @NotNull Maze maze;

    public RecBack(@NotNull MazeDim mazeDim) {
        maze = new Maze(mazeDim);
    }

    public void generateMaze() {
        Cell startCell = maze.getRandomCell(random);
        generateMazeFrom(startCell);
    }

    private void generateMazeFrom(@NotNull Cell currentCell) {
        visited.add(currentCell);

        final List<Cell> unvisitedNeighbors = getUnvisitedNeighbors(currentCell);
        Collections.shuffle(unvisitedNeighbors, random);

        for (Cell neighbor : unvisitedNeighbors) {
            if (!visited.contains(neighbor)) {
                maze.addPass(currentCell, Set.of(neighbor));
                generateMazeFrom(neighbor);
            }
        }
    }

    private List<Cell> getUnvisitedNeighbors(@NotNull Cell cell) {
        return maze.getAllNeighbors(cell).filter(c -> !visited.contains(c)).toList();
    }
}
