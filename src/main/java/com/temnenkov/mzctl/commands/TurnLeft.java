package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "a", description = "Повернуться налево")
public class TurnLeft implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userLogin;

    private final GameEngine gameEngine;

    public TurnLeft(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        gameEngine.turnLeft(userLogin);
        System.out.println(gameEngine.describeEnvironment(userLogin));
    }
}
