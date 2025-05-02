package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.DisjointSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomizedKruskalMazeGenerator implements MazeGenerator {

    private final Maze maze;
    private final Random random;
    private boolean generated = false;

    public RandomizedKruskalMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.maze = MazeFactory.createNotConnectedMaze(mazeDim);
        this.random = random;
    }

    @Override
    public Maze generateMaze() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;

        final DisjointSet<Cell> disjointSet = new DisjointSet<>();
        maze.forEach(disjointSet::makeSet);

        List<Wall> walls = getAllWalls();
        Collections.shuffle(walls, random);

        for (Wall wall : walls) {
            Cell cell1 = wall.cell1();
            Cell cell2 = wall.cell2();

            if (disjointSet.find(cell1) != disjointSet.find(cell2)) {
                maze.addPass(cell1, cell2);
                disjointSet.union(cell1, cell2);
            }
        }

        return maze;
    }

    private @NotNull List<Wall> getAllWalls() {
        List<Wall> walls = new ArrayList<>();

        maze.forEach(cell -> maze.getAllNeighbors(cell)
                .forEach(neighbor -> walls.add(new Wall(cell, neighbor))));

        return walls;
    }

    private record Wall(Cell cell1, Cell cell2) {}
}