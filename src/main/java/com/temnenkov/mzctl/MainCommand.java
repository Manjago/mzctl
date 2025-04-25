package com.temnenkov.mzctl;


import picocli.CommandLine;

import java.util.Scanner;
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
public class MainCommand implements Runnable  {

    public static void main(String[] args) {
        final CommandLine cmd = new CommandLine(new MainCommand());

        if (args.length > 0) {
            // execute single command and exit
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        } else {
            //start REPL
            final LineReader reader = LineReaderBuilder.builder().build();
            System.out.println("Welcome to Maze REPL. Type '/quit' to exit.");

            while (true) {
                String line;
                try {
                    line = reader.readLine("> ").trim();
                    if ("/quit".equalsIgnoreCase(line)) {
                        System.out.println("Goodbye!");
                        break;
                    }
                    if (!line.isEmpty()) {
                        cmd.execute(line.split("\\s+"));
                    }
                } catch (UserInterruptException | EndOfFileException e) {
                    System.out.println("\nGoodbye!");
                    break;
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
    @Override
    public void run() {
        // no command branch
        CommandLine.usage(this, System.out);
    }
}
