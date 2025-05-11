package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "d", description = "Повернуться направо")
public class TurnRight implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userLogin;

    private final GameEngine gameEngine;

    public TurnRight(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        gameEngine.turnRight(userLogin);
        System.out.println(gameEngine.describeEnvironment(userLogin));
    }
}
