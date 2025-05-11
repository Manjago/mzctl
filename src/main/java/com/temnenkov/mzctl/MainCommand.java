package com.temnenkov.mzctl;

import com.temnenkov.mzctl.commands.GenerateMaze;
import com.temnenkov.mzctl.context.SimpleContextHolder;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;

@CommandLine.Command(
        name = "mzctl",
        description = "Maze CLI utility",
        mixinStandardHelpOptions = true,
        subcommands = {
                GenerateMaze.class,
                MainCommand.LoadMaze.class,
                MainCommand.MoveForward.class,
                MainCommand.TurnLeft.class,
                MainCommand.TurnRight.class,
                MainCommand.TurnBack.class,
                MainCommand.WhereAmI.class
        }
)
public class MainCommand implements Runnable {

    private final MazeManager mazeManager;
    private PlayerStateND playerState;
    private MazeEnvironmentDescriber describer;

    public MainCommand() throws IOException {
        mazeManager = new MazeManager(Path.of("mazes"));
    }

    public static void main(String[] args) throws IOException {

        SimpleContextHolder.INSTANCE.getSimpleContext().setMazeManager(new MazeManager(Path.of("mazes")));

        final CommandLine cmd = new CommandLine(new MainCommand());

        if (args.length > 0) {
            executeSingleCommand(cmd, args);
        } else {
            startRepl(cmd);
        }
    }

    private static void executeSingleCommand(@NotNull CommandLine cmd, String[] args) {
        System.exit(cmd.execute(args));
    }

    private static void startRepl(CommandLine cmd) {
        final LineReader reader = LineReaderBuilder.builder().build();
        System.out.println("Welcome to Maze REPL. Type '/quit' to exit.");

        while (true) {
            String line = readLine(reader);
            if (line == null || "/quit".equalsIgnoreCase(line)) {
                System.out.println("Goodbye!");
                break;
            }
            if (!line.isEmpty()) {
                executeReplCommand(cmd, line);
            }
        }
    }

    private static String readLine(LineReader reader) {
        try {
            return reader.readLine("> ").trim();
        } catch (UserInterruptException | EndOfFileException e) {
            return null;
        }
    }

    private static void executeReplCommand(CommandLine cmd, String line) {
        try {
            CommandLine.ParseResult parseResult = cmd.parseArgs(line.split("\\s+"));
            cmd.execute(parseResult.originalArgs().toArray(new String[0]));
        } catch (CommandLine.UnmatchedArgumentException e) {
            System.out.println("Unknown command: " + line);
            printAvailableCommands(cmd);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printAvailableCommands(@NotNull CommandLine cmd) {
        System.out.println("Available commands:");
        cmd.getSubcommands().keySet().forEach(c -> System.out.println("  " + c));
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    private boolean isMazeLoaded() {
        if (playerState == null || describer == null) {
            System.out.println("Ошибка: лабиринт не загружен. Сначала загрузите лабиринт командой 'load-maze'.");
            return false;
        }
        return true;
    }

    @CommandLine.Command(name = "load-maze", description = "Загружает лабиринт и помещает игрока в случайную комнату")
    public static class LoadMaze implements Runnable {
        @CommandLine.Option(names = {"-n", "--name"}, required = true) String name;

        @Override
        public void run() {
            final MazeManager mazeManager = MazeManager.getInstance();
            final Maze maze = mazeManager.loadMaze(name);
            final PlayerStateND playerState = mazeManager.createPlayerInRandomPosition(maze);
            final MazeEnvironmentDescriber describer = new MazeEnvironmentDescriber(maze);
            System.out.println("Лабиринт '" + name + "' загружен.");
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @CommandLine.Command(name = "w", description = "Идти вперед")
    public class MoveForward implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.moveForward();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @CommandLine.Command(name = "a", description = "Повернуться налево")
    public class TurnLeft implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.rotateCounterClockwise2D();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @CommandLine.Command(name = "d", description = "Повернуться направо")
    public class TurnRight implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.rotateClockwise2D();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @CommandLine.Command(name = "s", description = "Повернуться назад")
    public class TurnBack implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            playerState.opposite();
            System.out.println(describer.describeEnvironment(playerState));
        }
    }

    @CommandLine.Command(name = "?", description = "Повторить описание окружения")
    public class WhereAmI implements Runnable {
        @Override
        public void run() {
            if (!isMazeLoaded()) return;
            System.out.println(describer.describeEnvironment(playerState));
        }
    }
}