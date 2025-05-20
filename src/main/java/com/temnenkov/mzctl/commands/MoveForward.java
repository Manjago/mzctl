package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "w", description = "Идти вперед")
public class MoveForward implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userId;

    private final GameEngine gameEngine;

    public MoveForward(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        final String resolvedUserId = GameContextHelper.getUserId(gameEngine.getContext(), userId);
        if (resolvedUserId == null) {
            System.out.println("Ошибка: сначала авторизуйтесь через команду login");
            return;
        }
        gameEngine.moveForward(resolvedUserId);
        System.out.println(gameEngine.describeEnvironment(resolvedUserId));
    }
}
