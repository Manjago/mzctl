package com.temnenkov.mzctl;


import picocli.CommandLine;

@CommandLine.Command(
        subcommands = {
                Generate1Command.class,
                Generate2Command.class
        }
)
public class MainCommand implements Runnable  {

    public static void main(String[] args) {
        new CommandLine(new MainCommand()).execute(args);
    }

    @Override
    public void run() {
        System.out.println("Hello World!");
    }
}
