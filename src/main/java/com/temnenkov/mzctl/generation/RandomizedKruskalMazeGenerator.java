package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.DisjointSet;
import com.temnenkov.mzctl.util.IndexedHashSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

        final IndexedHashSet<Wall> walls = getAllWalls();
        walls.shuffle(random);  // теперь просто вызываем shuffle

        for (Wall wall : walls) { // теперь просто перебираем элементы набора
            Cell cell1 = wall.cell1();
            Cell cell2 = wall.cell2();

            if (disjointSet.find(cell1) != disjointSet.find(cell2)) {
                maze.addPass(cell1, cell2);
                disjointSet.union(cell1, cell2);
            }
        }

        return maze;
    }

    private @NotNull IndexedHashSet<Wall> getAllWalls() {
        final IndexedHashSet<Wall> walls = new IndexedHashSet<>();

        maze.forEach(cell -> maze.getAllNeighbors(cell)
                .forEach(neighbor -> walls.add(createWall(cell, neighbor))));

        return walls;
    }

    private record Wall(Cell cell1, Cell cell2) {}

    @Contract("_, _ -> new")
    private @NotNull Wall createWall(@NotNull Cell cell1, @NotNull Cell cell2) {
        if (cell1.hashCode() <= cell2.hashCode()) {
            return new Wall(cell1, cell2);
        } else {
            return new Wall(cell2, cell1);
        }
    }
}