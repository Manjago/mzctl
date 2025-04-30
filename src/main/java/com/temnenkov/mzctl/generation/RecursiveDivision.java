package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.model.Slice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class RecursiveDivision {

    private final @NotNull Maze maze;
    private final @NotNull Random random;
    private boolean generated = false;

    public RecursiveDivision(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.maze = MazeFactory.createFullConnectedMaze(mazeDim);
        this.random = random;
    }

    @NotNull
    public Maze generateMaze() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;


        final Cell top = new Cell(maze.getMazeDimension().dimensions().stream().map(i -> 0).toList());
        final Cell bottom = new Cell(maze.getMazeDimension().dimensions().stream().map(i -> i-1).toList());
        final Queue<Slice> queue = new ArrayDeque<>();
        queue.add(new Slice(top, bottom));

        while(!queue.isEmpty()) {
            final Slice slice = queue.remove();

            final Wall wall = createRandomWallInSlice(slice);
            if (wall != null) {
                queue.addAll(buildWall(slice, wall));
            }
        }

        return maze;
    }

    private @NotNull List<Slice> buildWall(@NotNull Slice slice, @NotNull Wall wall ) {
        //получаем все клетки из стены
        final List<Cell> wallCells = slice.stream().filter(cell -> cell.coord(wall.dimensionNum) == wall.dimensionValue)
                .toList();
        //для каждой из них строим стену
        wallCells.forEach(cell -> maze.removePass(cell, cell.plus(wall.dimensionNum, 1)));
        // выбираем в стене одну случайную клетку
        final Cell passCell = wallCells.get(random.nextInt(wallCells.size()));
        // и делаем в ней проход
        maze.addPass(passCell, Set.of(passCell.plus(wall.dimensionNum, 1)));
        // и возвращаем slice разбитый на 2

        final List<Slice> result = new ArrayList<>();

        final Cell first1 = slice.first;
        final Cell last1 = slice.last.withNewDimensionValue(wall.dimensionNum, wall.dimensionValue);
        result.add(new Slice(first1, last1));

        final Cell first2 = slice.first.withNewDimensionValue(wall.dimensionNum, wall.dimensionValue+1);
        final Cell last2 = slice.last;
        result.add(new Slice(first2, last2));
        return result;
    }

    @Nullable
    private Wall createRandomWallInSlice(@NotNull Slice slice) {
        final List<Wall> pretenders = new ArrayList<>();
        for(int dimensionNum =0; dimensionNum< slice.first.size(); ++dimensionNum ) {
            final int topCoord = slice.first.coord(dimensionNum);
            final int lastCoord = slice.last.coord(dimensionNum);
            if (lastCoord - topCoord > 0) {
                int cutIndex = topCoord + random.nextInt(lastCoord - topCoord);
                pretenders.add(new Wall(dimensionNum, cutIndex));
            }
        }

        if (pretenders.isEmpty()) {
            return null;
        } else {
           return pretenders.get(random.nextInt(pretenders.size()));
        }
    }

    private static class Wall {
        final int dimensionNum;
        final int dimensionValue;

        private Wall(int dimensionNum, int dimensionValue) {
            this.dimensionNum = dimensionNum;
            this.dimensionValue = dimensionValue;
        }
    }

}
