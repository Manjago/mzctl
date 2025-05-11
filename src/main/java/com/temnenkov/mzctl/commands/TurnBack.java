package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "s", description = "Повернуться назад")
public class TurnBack implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userLogin;

    private final GameEngine gameEngine;

    public TurnBack(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        gameEngine.turnBack(userLogin);
        System.out.println(gameEngine.describeEnvironment(userLogin));
    }
}
