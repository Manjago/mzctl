package com.temnenkov.mzctl.visualization;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MazeAsciiVisualizer {

    private final Maze maze;
    private final int width;
    private final int height;

    public MazeAsciiVisualizer(@NotNull Maze maze) {
        if (maze.getMazeDimension().size() != 2) {
            throw new IllegalArgumentException("Only 2-dimensional mazes supported");
        }
        this.maze = maze;
        this.height = maze.getMazeDimension().dimSize(0);
        this.width = maze.getMazeDimension().dimSize(1);
    }

    public void printMaze() {
        System.out.print(mazeToString(null, null));
    }

    public void printMaze(@NotNull Cell playerPosition, @NotNull Facing playerFacing) {
        System.out.print(mazeToString(playerPosition, playerFacing));
    }

    @NotNull
    public String mazeToString(@Nullable Cell playerPosition, @Nullable Facing playerFacing) {
        return buildMazeString(playerPosition, playerFacing);
    }

    @NotNull
    public String mazeToString() {
        return buildMazeString(null, null);
    }

    private @NotNull String buildMazeString(@Nullable Cell playerPosition, @Nullable Facing playerFacing) {
        StringBuilder sb = new StringBuilder();

        // верхняя граница лабиринта
        sb.append("+");
        sb.append("---+".repeat(Math.max(0, width)));
        sb.append("\n");

        for (int y = 0; y < height; y++) {
            // левая стена первого столбца
            sb.append("|");

            // горизонтальный проход между комнатами
            for (int x = 0; x < width; x++) {
                Cell current = Cell.of(y, x);

                if (playerFacing != null && current.equals(playerPosition)) {
                    // рисуем игрока с направлением
                    sb.append(" ").append(facingSymbol(playerFacing)).append(" ");
                } else {
                    sb.append("   ");
                }

                // если проход вправо есть, не рисуем стену справа
                if (x < width - 1 && maze.canPass(current, Cell.of(y, x + 1))) {
                    sb.append(" ");
                } else {
                    sb.append("|");
                }
            }
            sb.append("\n");

            // стены под текущим рядом комнат
            sb.append("+");
            for (int x = 0; x < width; x++) {
                Cell current = Cell.of(y, x);
                // если проход вниз есть, не рисуем стену снизу
                if (y < height - 1 && maze.canPass(current, Cell.of(y + 1, x))) {
                    sb.append("   +");
                } else {
                    sb.append("---+");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // Вспомогательный метод для получения символа направления
    private char facingSymbol(@NotNull Facing facing) {
        if (facing.equals(Facing.NORTH)) return '^';
        if (facing.equals(Facing.SOUTH)) return 'V';
        if (facing.equals(Facing.WEST)) return '<';
        if (facing.equals(Facing.EAST)) return '>';
        return '?'; // по умолчанию, если направление неизвестно
    }
}