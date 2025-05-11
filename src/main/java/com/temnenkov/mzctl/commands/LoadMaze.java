package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
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
    String userLogin;

    private final GameEngine gameEngine;

    public LoadMaze(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try {
            gameEngine.loadMaze(name, userLogin);
            System.out.println("Лабиринт '" + name + "' загружен.");
            System.out.println(gameEngine.describeEnvironment(userLogin));
        } catch (MazeSerializationException e) {
            logger.error("Fail load maze", e);
            System.out.println("Ошибка: лабиринт '" + name + "' не найден или повреждён.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            System.out.println("Произошла неизвестная ошибка: " + e.getMessage());
        }
    }
}
