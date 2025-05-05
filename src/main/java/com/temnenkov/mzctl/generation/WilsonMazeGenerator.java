package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.IndexedHashSet;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WilsonMazeGenerator implements MazeGenerator {

    private static final String CTOR = ".ctor";

    private final Maze maze;
    private final Random random;
    private boolean generated = false;
    private final Set<Cell> inMaze = new HashSet<>();

    public WilsonMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.maze = MazeFactory.createNotConnectedMaze(SimplePreconditions.checkNotNull(mazeDim, "mazeDim", CTOR));
        this.random = SimplePreconditions.checkNotNull(random, "random", CTOR);
    }

    @Override
    public Maze generateMaze() {
        checkAlreadyGenerated();

        final List<Cell> allCells = maze.stream().toList();
        final Cell firstCell = allCells.get(random.nextInt(allCells.size()));
        inMaze.add(firstCell);

        final IndexedHashSet<Cell> notInMaze = new IndexedHashSet<>(allCells);
        notInMaze.remove(firstCell);

        while (!notInMaze.isEmpty()) {
            Cell current = notInMaze.getRandom(random);
            final Map<Cell, Cell> path = new LinkedHashMap<>();  // сохраняем порядок добавления

            // Случайное блуждание
            while (!inMaze.contains(current)) {
                final Cell next = pickRandomNeighbor(current);
                path.put(current, next);
                current = next;

                // удаление петель
                if (path.containsKey(current)) {
                    final Iterator<Cell> it = path.keySet().iterator();
                    while (it.hasNext()) {
                        if (it.next().equals(current)) {
                            break;
                        }
                        it.remove();
                    }
                }
            }

            // Добавление пути в лабиринт
            current = path.keySet().iterator().next();
            while (path.containsKey(current)) {
                final Cell next = path.get(current);
                maze.addPass(current, next);
                inMaze.add(current);
                notInMaze.remove(current);
                current = next;
            }
        }

        return maze;
    }

    private void checkAlreadyGenerated() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;
    }

    private Cell pickRandomNeighbor(Cell cell) {
        final List<Cell> neighbors = maze.getAllNeighbors(cell).toList();
        return neighbors.get(random.nextInt(neighbors.size()));
    }

}