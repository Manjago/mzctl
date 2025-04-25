package com.temnenkov.mzctl;


import picocli.CommandLine;

import java.util.Scanner;

@CommandLine.Command(
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
            cmd.execute(line.split("\\s+"));
        }

        scanner.close();
    }

    @Override
    public void run() {
        if (args == null || args.length == 0) {
            System.out.println("No command provided.");
            return;
        }

        String command = args[0];
        System.out.println("Unknown command: " + command);
    }
}
