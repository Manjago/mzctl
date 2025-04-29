package com.temnenkov.mzctl.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class MazeFactory {

    private MazeFactory() {
    }

    @NotNull
    public static Maze createNotConnectedMaze(@NotNull MazeDim mazeDimension) {
        return new Maze(mazeDimension);
    }

    @NotNull
    public static Maze createNotConnectedMaze(int... dimensions) {
        return new Maze(MazeDim.of(dimensions));
    }

    @NotNull
    public static Maze createFullConnectedMaze(@NotNull MazeDim mazeDimension) {
        final Maze maze = createNotConnectedMaze(mazeDimension);
        return connectAll(maze);

    }

    @NotNull
    public static Maze createFullConnectedMaze(int... dimensions) {
        final Maze maze = createNotConnectedMaze(dimensions);
        return connectAll(maze);
    }

    @Contract("_ -> param1")
    private static @NotNull Maze connectAll(@NotNull Maze maze) {
        for(Cell cell: maze) {
            final Set<Cell> neighbours = maze.getAllNeighbors(cell).collect(Collectors.toSet());
            maze.addPass(cell, neighbours);
        }

        return maze;
    }

}
