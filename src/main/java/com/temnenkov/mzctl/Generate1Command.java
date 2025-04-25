package com.temnenkov.mzctl;

import picocli.CommandLine;

@CommandLine.Command(name = "gen1")
public class Generate1Command implements Runnable {
    @Override
    public void run() {
        System.out.println("Generating 1...");
    }
}
