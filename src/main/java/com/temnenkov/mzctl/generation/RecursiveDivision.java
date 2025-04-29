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
            if (wall == null) {
                continue;
            }


        }

        return maze;
    }

    private void buildWall(@NotNull Slice slice, @NotNull Wall wall ) {

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
