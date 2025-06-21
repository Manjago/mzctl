package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
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

    public Path getMazeDirectory() {
        return mazeDirectory;
    }

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
        saveMaze(name, maze, mazeDirectory);
    }

    /**
     * Загружает лабиринт из файла с расширением .mzpack.
     *
     * @param name имя файла (без расширения)
     * @return загруженный лабиринт
     */
    public Maze loadMaze(@NotNull String name) {
        return loadMaze(name, mazeDirectory);
    }

    public void saveUserMaze(String userId, String mazeName, Maze maze) throws IOException {
        final Path userMazeDir = getUserMazeDir(userId);
        saveMaze(mazeName, maze, userMazeDir);
    }

    private @NotNull Path getUserMazeDir(String userId) throws IOException {
        final Path userMazeDir = mazeDirectory.resolve("users").resolve(userId).resolve("mazes");
        Files.createDirectories(userMazeDir);
        return userMazeDir;
    }

    private static void saveMaze(String mazeName, Maze maze, @NotNull Path userMazeDir) {
        final String filename = userMazeDir.resolve(mazeName + ".mzpack").toString();
        SerializationHelper.saveMazeToFile(maze, filename);
    }

    public Maze loadUserMaze(String userId, String mazeName) throws IOException {
        final Path userMazeDir = getUserMazeDir(userId);
        return loadMaze(mazeName, userMazeDir);
    }

    private static @NotNull Maze loadMaze(String mazeName, @NotNull Path userMazeDir) {
        final String filename = userMazeDir.resolve(mazeName + ".mzpack").toString();
        return SerializationHelper.loadMazeFromFile(filename);
    }

    private static ThreadLocalRandom getLocalRandom() {
        return ThreadLocalRandom.current();
    }
}