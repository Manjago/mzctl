package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;

public class RecursiveDivision {

    private final @NotNull Maze maze;
    private boolean generated = false;

    public RecursiveDivision(@NotNull MazeDim mazeDim) {
        this.maze = MazeFactory.createNotConnectedMaze(mazeDim);
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

        }

        return maze;
    }

    private static class Slice {
        private final Cell first;
        private final Cell last;

        private Slice(Cell first, Cell last) {
            this.first = first;
            this.last = last;
        }
    }

}
