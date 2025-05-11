package com.temnenkov.mzctl;

import com.temnenkov.mzctl.commands.GenerateMaze;
import com.temnenkov.mzctl.commands.LoadMaze;
import com.temnenkov.mzctl.commands.MoveForward;
import com.temnenkov.mzctl.commands.TurnBack;
import com.temnenkov.mzctl.commands.TurnLeft;
import com.temnenkov.mzctl.commands.TurnRight;
import com.temnenkov.mzctl.commands.WhereAmI;
import com.temnenkov.mzctl.commands.util.CommandFactory;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.context.SimpleGameContext;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.gameengine.GameEngineImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
                LoadMaze.class,
                MoveForward.class,
                TurnLeft.class,
                TurnRight.class,
                TurnBack.class,
                WhereAmI.class,
                CommandLine.HelpCommand.class
        }
)
public class MainCommand implements Runnable {

    public MainCommand() throws IOException {
        // нужен?
    }

    public static void main(String[] args) throws IOException {

        final GameContext context = new SimpleGameContext(new MazeManager(Path.of("mazes")));

        final GameEngine gameEngine = new GameEngineImpl(context);
        final CommandFactory factory = new CommandFactory(gameEngine); // <-- передаем его в фабрику
        final CommandLine cmd = new CommandLine(new MainCommand(), factory);


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

    private static @Nullable String readLine(@NotNull LineReader reader) {
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

}