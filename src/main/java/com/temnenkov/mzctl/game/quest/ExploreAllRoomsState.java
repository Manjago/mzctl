package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ExploreAllRoomsState implements QuestState {
    private final Set<Cell> visitedCells = new HashSet<>();
    private final int totalCells;

    public ExploreAllRoomsState(@NotNull Maze maze) {
        this.totalCells = maze.totalCellCount();
    }

    public void markVisited(Cell cell) {
        visitedCells.add(cell);
    }

    public int visitedCount() {
        return visitedCells.size();
    }

    public int remainingCount() {
        return totalCells - visitedCells.size();
    }

    public boolean isVisited(Cell cell) {
        return visitedCells.contains(cell);
    }

    public boolean allVisited() {
        return visitedCells.size() == totalCells;
    }
}