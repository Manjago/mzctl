package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.serialize.SerializationHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Управляет генерацией, сохранением и загрузкой лабиринтов.
 *
 * <p>В текущей версии поддерживаются только 2D-лабиринты.</p>
 * */
public class MazeManager {

    private final Path mazeDirectory;
    private static final Facing[] DIRECTIONS = {Facing.NORTH, Facing.SOUTH, Facing.EAST, Facing.WEST};

    /**
     * Создаёт MazeManager, работающий с лабиринтами в заданной директории.
     *
     * @param mazeDirectory директория для сохранения и загрузки лабиринтов
     * @throws IOException если директория не существует и не может быть создана
     */
    public MazeManager(@NotNull Path mazeDirectory) throws IOException {
        if (!Files.exists(mazeDirectory)) {
            Files.createDirectories(mazeDirectory);
        }
        this.mazeDirectory = mazeDirectory;
    }

    /**
     * Генерирует двумерный лабиринт.
     *
     * @param width ширина лабиринта
     * @param height высота лабиринта
     * @param algo алгоритм генерации
     * @return сгенерированный лабиринт
     */
    public Maze generateMaze2D(int width, int height, MazeGeneratorFactory.Algo algo) {
        final MazeDim dim = MazeDim.of(width, height);
        return new MazeGeneratorFactory(getLocalRandom()).create(algo, dim).generateMaze();
    }

    /**
     * Сохраняет лабиринт в файл с расширением .mzpack.
     *
     * @param name имя файла (без расширения)
     * @param maze лабиринт
     */
    public void saveMaze(@NotNull String name, @NotNull Maze maze) {
        final String filename = mazeDirectory.resolve(name + ".mzpack").toString();
        SerializationHelper.saveMazeToFile(maze, filename);
    }

    /**
     * Загружает лабиринт из файла с расширением .mzpack.
     *
     * @param name имя файла (без расширения)
     * @return загруженный лабиринт
     */
    public Maze loadMaze(@NotNull String name) {
        final String filename = mazeDirectory.resolve(name + ".mzpack").toString();
        return SerializationHelper.loadMazeFromFile(filename);
    }

    /**
     * Создаёт состояние игрока в случайной клетке лабиринта с случайным направлением взгляда.
     *
     * @param maze лабиринт
     * @return состояние игрока
     */
    public PlayerStateND createPlayerInRandomPosition(@NotNull Maze maze) {
        final Cell randomCell = maze.getRandomCell(getLocalRandom());
        final Facing randomFacing = randomFacing();
        return new PlayerStateND(randomCell, randomFacing);
    }

    private static ThreadLocalRandom getLocalRandom() {
        return ThreadLocalRandom.current();
    }

    private Facing randomFacing() {
        return DIRECTIONS[getLocalRandom().nextInt(DIRECTIONS.length)];
    }

}