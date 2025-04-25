package com.temnenkov.mzctl;


import picocli.CommandLine;

import java.util.Scanner;

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

    @CommandLine.Parameters(index = "0", description = "Command to execute", arity = "0..*")
    private String[] args;

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final CommandLine cmd = new CommandLine(new MainCommand());

        System.out.println("Welcome to Maze REPL. Type '/quit' to exit.");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if ("/quit".equalsIgnoreCase(line)) {
                System.out.println("Goodbye!");
                break;
            }

            if (!line.isEmpty()) {
                String[] arguments = line.split("\\s+");
                try {
                    cmd.execute(arguments);
                } catch (CommandLine.UnmatchedArgumentException e) {
                    System.out.println("Unknown command or invalid arguments. Type '--help' for usage.");
                } catch (Exception e) {
                    System.out.println("Error executing command: " + e.getMessage());
                }
            }
        }

        scanner.close();
    }

    @Override
    public void run() {
        // no command branch
        CommandLine.usage(this, System.out);
    }
}
