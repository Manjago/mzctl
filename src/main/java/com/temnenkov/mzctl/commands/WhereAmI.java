package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "?", description = "Повторить описание окружения")
public class WhereAmI implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userLogin;

    private final GameEngine gameEngine;

    public WhereAmI(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        gameEngine.describeEnvironment(userLogin);
    }
}
