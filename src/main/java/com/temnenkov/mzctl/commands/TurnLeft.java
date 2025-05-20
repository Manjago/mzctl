package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.commands.util.GameContextHelper;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

@CommandLine.Command(name = "a", description = "Повернуться налево")
public class TurnLeft implements Runnable {

    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userId;

    private final GameEngine gameEngine;
    private final GameContext gameContext;

    public TurnLeft(GameEngine gameEngine, GameContext gameContext) {
        this.gameEngine = gameEngine;
        this.gameContext = gameContext;
    }

    @Override
    public void run() {
        final String resolvedUserId = GameContextHelper.getUserId(gameContext, userId);
        if (resolvedUserId == null) {
            System.out.println("Ошибка: сначала авторизуйтесь через команду login");
            return;
        }
        gameEngine.turnLeft(resolvedUserId);
        System.out.println(gameEngine.describeEnvironment(resolvedUserId));
    }
}
