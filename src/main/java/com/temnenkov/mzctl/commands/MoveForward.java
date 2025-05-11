package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "w", description = "Идти вперед")
public class MoveForward implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userLogin;

    private final GameEngine gameEngine;

    public MoveForward(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        gameEngine.moveForward(userLogin);
        System.out.println(gameEngine.describeEnvironment(userLogin));
    }
}
