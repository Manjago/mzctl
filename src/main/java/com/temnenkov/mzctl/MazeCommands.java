package com.temnenkov.mzctl;

import com.temnenkov.mzctl.game.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.PlayerStateND;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.Maze;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Path;

@Command(name = "", description = "Maze Game REPL", subcommands = {
        MazeCommands.GenerateMaze.class,
        MazeCommands.LoadMaze.class,
        MazeCommands.MoveForward.class,
        MazeCommands.TurnLeft.class,
        MazeCommands.TurnRight.class,
        MazeCommands.TurnBack.class,
        MazeCommands.WhereAmI.class
})
public class MazeCommands {

    private final MazeManager mazeManager = new MazeManager(Path.of("mazes"));
    private PlayerStateND playerState;
    private MazeEnvironmentDescriber describer;

    public MazeCommands() throws IOException {
        // PicoCLI требует наличие пустого конструктора
    }

    private boolean isMazeLoaded() {
        if (playerState == null || describer == null) {
            System.out.println("Ошибка: лабиринт не загружен. Сначала загрузите лабиринт командой 'load-maze'.");
            return false;
        }
        return true;
    }

    @Command(name = "generate-maze", description = "Генерирует и сохраняет лабиринт")
    public class GenerateMaze implements Runnable {
        @Option(names = {"-n", "--name"}, required = true) String name;
        @Option(names = {"-w", "--width"}, required = true) int width;
        @Option(names = {"-h", "--height"}, required = true) int height;
        @Option(names = {"-a", "--algo"}, required = true) MazeGeneratorFactory.Algo algo;

        @Override
        public void run() {
            Maze maze = mazeManager.generateMaze2D(width, height, algo);
            mazeManager.saveMaze(name, maze);
            System.out.println("Лабиринт '" + name + "' создан и сохранён.");
        }
    }

    @Command(name = "load-maze", description = "Загружает лабиринт и помещает игрока в случайную комнату")
    public class LoadMaze implements Runnable {
        @Option(names = {"-n", "--name"}, required = true) String name;

        @Override
        public void run() {
            Maze maze = mazeManager.loadMaze(name);
            playerState = mazeManager.createPlayerInRandomPosition(maze);
            describer = new MazeEnvironmentDescriber(maze);
            System.out.println("Лабиринт '" + name + "' загружен.");
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @Command(name = "w", description = "Идти вперед")
    public class MoveForward implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.moveForward();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @Command(name = "a", description = "Повернуться налево")
    public class TurnLeft implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.rotateCounterClockwise2D();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @Command(name = "d", description = "Повернуться направо")
    public class TurnRight implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.rotateClockwise2D();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @Command(name = "s", description = "Повернуться назад")
    public class TurnBack implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.opposite();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @Command(name = "?", description = "Повторить описание окружения")
    public class WhereAmI implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            System.out.println(describer.describeEnvironment(playerState));
        }
    }
}