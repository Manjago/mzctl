package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.commands.util.GameContextHelper;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.model.UserId;
import com.temnenkov.mzctl.model.serialize.MazeSerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "load-maze", description = "Загружает лабиринт и помещает игрока в случайную комнату")
public class LoadMaze implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LoadMaze.class);

    @CommandLine.Option(names = {"-n", "--name"}, required = true)
    String name;
    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userId;

    private final GameEngine gameEngine;
    private final GameContext gameContext;

    public LoadMaze(GameEngine gameEngine, GameContext gameContext) {
        this.gameEngine = gameEngine;
        this.gameContext = gameContext;
    }

    @Override
    public void run() {
        try {
            final UserId resolvedUserId = GameContextHelper.resolveUserId(gameContext, userId);
            if (resolvedUserId == null) {
                return;
            }
            gameEngine.loadMaze(resolvedUserId, name);
            System.out.println("Лабиринт '" + name + "' загружен.");
            System.out.println(gameEngine.describeEnvironment(resolvedUserId));
        } catch (MazeSerializationException e) {
            logger.error("Fail load maze", e);
            System.out.println("Ошибка: лабиринт '" + name + "' не найден или повреждён.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            System.out.println("Произошла неизвестная ошибка: " + e.getMessage());
        }
    }
}
