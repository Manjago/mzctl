package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.serialize.MazeSerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "load-maze", description = "Загружает лабиринт и помещает игрока в случайную комнату")
public class LoadMaze implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LoadMaze.class);

    @CommandLine.Option(names = {"-n", "--name"}, required = true)
    String name;

    private final GameContext context;

    public LoadMaze(GameContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            final MazeManager mazeManager = context.getMazeManager();
            final Maze maze = mazeManager.loadMaze(name);
            final PlayerStateND playerState = mazeManager.createPlayerInRandomPosition(maze);
            final MazeEnvironmentDescriber describer = new MazeEnvironmentDescriber(maze);
            // временно один логин, потом их будет много
            final PlayerSession playerSession = new PlayerSession("test", maze, describer, playerState, null);
            context.createPlayerSession(playerSession);
            System.out.println("Лабиринт '" + name + "' загружен.");
            System.out.println(describer.describeEnvironment(playerState));
        } catch (MazeSerializationException e) {
            logger.error("Fail load maze", e);
            System.out.println("Ошибка: лабиринт '" + name + "' не найден или повреждён.");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            System.out.println("Произошла неизвестная ошибка: " + e.getMessage());
        }
    }
}
