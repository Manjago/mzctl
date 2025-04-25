package com.temnenkov.mzctl;

import picocli.CommandLine;

@CommandLine.Command(name = "gen2")
public class Generate2Command implements Runnable {
    @Override
    public void run() {
        System.out.println("Generating 2...");
    }
}
