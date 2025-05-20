package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "d", description = "Повернуться направо")
public class TurnRight implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userId;

    private final GameEngine gameEngine;

    public TurnRight(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        final String resolvedUserId = GameContextHelper.getUserId(gameEngine.getContext(), userId);
        if (resolvedUserId == null) {
            System.out.println("Ошибка: сначала авторизуйтесь через команду login");
            return;
        }
        gameEngine.turnRight(resolvedUserId);
        System.out.println(gameEngine.describeEnvironment(resolvedUserId));
    }
}
