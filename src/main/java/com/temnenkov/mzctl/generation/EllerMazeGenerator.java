package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.DisjointSet;
import com.temnenkov.mzctl.util.SimplePreconditions;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EllerMazeGenerator implements MazeGenerator {

    private static final String CTOR = ".ctor";
    private static final Logger log = LoggerFactory.getLogger(EllerMazeGenerator.class);

    private final Maze maze;
    private final Random random;
    private boolean generated = false;

    public EllerMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.maze = MazeFactory.createNotConnectedMaze(SimplePreconditions.checkNotNull(mazeDim, "mazeDim", CTOR));
        this.random = SimplePreconditions.checkNotNull(random, "random", CTOR);
    }

    @Override
    public Maze generateMaze() {
        checkAlreadyGenerated();

        final int layers = maze.getMazeDimension().dimSize(0);
        final List<Integer> dims = maze.getMazeDimension().dimensions();

        DisjointSet<Cell> disjointSet = new DisjointSet<>();

        for (int layer = 0; layer < layers; layer++) {
            final int finalLayer = layer;
            final List<Cell> currentLayerCells = maze.stream()
                    .filter(cell -> cell.getCoordinates()[0] == finalLayer)
                    .toList();

            // Добавляем ячейки текущего слоя в disjointSet (если еще не добавлены)
            currentLayerCells.forEach(cell -> {
                if (!disjointSet.contains(cell)) {
                    disjointSet.makeSet(cell);
                }
            });

            // Горизонтальные соединения текущего слоя
            for (Cell cell : currentLayerCells) {
                for (int dim = 1; dim < dims.size(); dim++) {
                    final Cell neighbor = cell.plusOne(dim);
                    if (contains(neighbor)
                            && !disjointSet.find(cell).equals(disjointSet.find(neighbor))
                            && (layer == layers - 1 || random.nextBoolean())) {
                        maze.addPass(cell, neighbor);
                        disjointSet.union(cell, neighbor);
                        log.trace("Горизонтальное соединение слоя {}: {} <-> {}", layer, cell, neighbor);
                    }
                }
            }

            if (layer == layers - 1) {
                break; // последний слой, вертикальные соединения вниз не нужны
            }

            // Вертикальные соединения вниз
            final Map<Cell, List<Cell>> sets = new HashMap<>();
            for (Cell cell : currentLayerCells) {
                final Cell setId = disjointSet.find(cell);
                sets.computeIfAbsent(setId, k -> new ArrayList<>()).add(cell);
            }

            for (List<Cell> setCells : sets.values()) {
                Collections.shuffle(setCells, random);
                int connections = 1 + random.nextInt(setCells.size()); // хотя бы одно соединение вниз
                for (int i = 0; i < connections; i++) {
                    Cell cell = setCells.get(i);
                    Cell belowCell = cell.plusOne(0);
                    if (contains(belowCell)) {
                        if (!disjointSet.contains(belowCell)) {
                            disjointSet.makeSet(belowCell);
                        }
                        maze.addPass(cell, belowCell);
                        disjointSet.union(cell, belowCell);
                        log.trace("Вертикальное соединение слоя {} вниз: {} <-> {}", layer, cell, belowCell);
                    }
                }
            }
        }

        return maze;
    }

    private boolean contains(@NotNull Cell cell) {
        for (int dim = 0; dim < maze.getMazeDimension().size(); dim++) {
            if (!maze.isValid(cell, dim)) {
                return false;
            }
        }
        return true;
    }

    private void checkAlreadyGenerated() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;
    }
}