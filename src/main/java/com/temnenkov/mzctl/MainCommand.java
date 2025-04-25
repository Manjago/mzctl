package com.temnenkov.mzctl;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.EndOfFileException;

@CommandLine.Command(
        name = "mzctl",
        description = "Maze CLI utility",
        mixinStandardHelpOptions = true,
        subcommands = {
                Generate1Command.class,
                Generate2Command.class
        }
)
public class MainCommand implements Runnable {

    public static void main(String[] args) {
        final CommandLine cmd = new CommandLine(new MainCommand());

        if (args.length > 0) {
            executeSingleCommand(cmd, args);
        } else {
            startRepl(cmd);
        }
    }

    private static void executeSingleCommand(CommandLine cmd, String[] args) {
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
}