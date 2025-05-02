package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import com.temnenkov.mzctl.util.IndexedHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
Начинаем с лабиринта, в котором все ячейки изолированы друг от друга
(все стены на месте).
Выбираем случайную стартовую ячейку и добавляем ее в множество "посещенных".
Затем на каждом шаге:
- Находим все стены, которые отделяют уже посещенные ячейки от непосещенных.
- Случайным образом выбираем одну из этих стен и удаляем её, соединяя посещенную и непосещенную ячейки.
- Добавляем новую ячейку в множество посещенных.
- Продолжаем до тех пор, пока не будут посещены все ячейки.
 */
public class RandomizedPrimMazeGenerator implements MazeGenerator {

    private final @NotNull Random random;
    private final @NotNull Maze maze;
    private boolean generated = false;
    private final Set<Cell> visited = new HashSet<>();
    private final IndexedHashSet<Wall> walls = new IndexedHashSet<>();

    public RandomizedPrimMazeGenerator(@NotNull MazeDim mazeDim, @NotNull Random random) {
        this.random = random;
        this.maze = MazeFactory.createNotConnectedMaze(mazeDim);
    }

    /**
     * Генерирует лабиринт с использованием Randomized Prim's algorithm.
     *
     * @return сгенерированный лабиринт
     * @throws IllegalStateException если метод был вызван повторно
     */
    @Override
    public Maze generateMaze() {
        if (generated) {
            throw new IllegalStateException("Maze already generated");
        }
        generated = true;

        // Выбираем случайную стартовую ячейку и добавляем ее в множество посещенных
        final Cell start = maze.getRandomCell(random);
        visited.add(start);

        // Добавляем стены стартовой ячейки в список стен
        getNeighborsWithWall(start).forEach(neighbor -> walls.add(new Wall(start, neighbor)));

        // Пока есть стены, которые можно удалить
        while (!walls.isEmpty()) {
            // Выбираем случайную стену из множества стен
            final Wall wall = walls.getRandom(random);
            walls.remove(wall);

            final Cell cell1 = wall.cell1();
            final Cell cell2 = wall.cell2();

            // Проверяем, что ровно одна из ячеек уже посещена
            if (visited.contains(cell1) && !visited.contains(cell2)) {
                visitCell(cell1, cell2);
            } else if (!visited.contains(cell1) && visited.contains(cell2)) {
                visitCell(cell2, cell1);
            }
            // Если обе ячейки уже посещены или обе не посещены, мы просто пропускаем эту стену
        }

        return maze;
    }

    private void visitCell(Cell from, Cell to) {
        maze.addPass(from, to);
        visited.add(to);
        getNeighborsWithWall(to).forEach(neighbor -> {
            if (!visited.contains(neighbor)) {
                walls.add(new Wall(to, neighbor));
            }
        });
    }

    private Stream<Cell> getNeighborsWithWall(@NotNull Cell cell) {
       return maze.getAllNeighbors(cell).filter(neighbor -> !maze.canPass(cell, neighbor));
    }

    private record Wall(@NotNull Cell cell1, @NotNull Cell cell2) {}
}
