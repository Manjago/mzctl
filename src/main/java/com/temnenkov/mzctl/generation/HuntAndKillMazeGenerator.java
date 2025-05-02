package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class HuntAndKillMazeGenerator implements MazeGenerator {

    private final Maze maze;
    private final Random random;
    private final Set<Cell> visited = new HashSet<>();
    private boolean generated = false;

    public HuntAndKillMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.maze = MazeFactory.createNotConnectedMaze(mazeDim);
        this.random = random;
    }

    @Override
    public Maze generateMaze() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;

        Cell current = maze.getRandomCell(random);
        visited.add(current);

        while (visited.size() < maze.totalCellCount()) {
            // Kill phase
            final List<Cell> unvisitedNeighbors = unvisitedNeighbors(current);
            if (!unvisitedNeighbors.isEmpty()) {
                final Cell neighbor = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                maze.addPass(current, neighbor);
                visited.add(neighbor);
                current = neighbor;
            } else {
                // Hunt phase
                Optional<Cell> nextCell = hunt();
                if (nextCell.isPresent()) {
                    current = nextCell.get();
                } else {
                    // Все ячейки посещены
                    break;
                }
            }
        }

        return maze;
    }

    /**
     * Возвращает список непосещенных соседей указанной ячейки.
     *
     * @param cell текущая ячейка
     * @return список непосещенных соседей
     */
    private List<Cell> unvisitedNeighbors(Cell cell) {
        return maze.getAllNeighbors(cell)
                .filter(neighbor -> !visited.contains(neighbor))
                .toList();
    }

    /**
     * Выполняет фазу "охоты" — ищет непосещенную ячейку, у которой есть хотя бы один посещенный сосед,
     * и соединяет ее с этим соседом.
     *
     * @return найденную ячейку, если такая есть, иначе Optional.empty()
     */
    private Optional<Cell> hunt() {
        // Ищем первую непосещенную ячейку с посещенным соседом
        return maze.stream()
                .filter(cell -> !visited.contains(cell))
                .filter(cell -> maze.getAllNeighbors(cell).anyMatch(visited::contains))
                .findAny()
                .map(cell -> {
                    final List<Cell> visitedNeighbors = maze.getAllNeighbors(cell)
                            .filter(visited::contains)
                            .toList();
                    final Cell neighbor = visitedNeighbors.get(random.nextInt(visitedNeighbors.size()));
                    maze.addPass(cell, neighbor);
                    visited.add(cell);
                    return cell;
                });
    }
}