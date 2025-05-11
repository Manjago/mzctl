package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Maze;
import picocli.CommandLine;

@CommandLine.Command(name = "load-maze", description = "Загружает лабиринт и помещает игрока в случайную комнату")
public class LoadMaze implements Runnable {
    @CommandLine.Option(names = {"-n", "--name"}, required = true)
    String name;

    @Override
    public void run() {
        final MazeManager mazeManager = MazeManager.getInstance();
        final Maze maze = mazeManager.loadMaze(name);
        final PlayerStateND playerState = mazeManager.createPlayerInRandomPosition(maze);
        final MazeEnvironmentDescriber describer = new MazeEnvironmentDescriber(maze);
        // временно один логин, потом их будет много
        final PlayerSession playerSession = new PlayerSession("test", maze, describer, playerState, null);
        PlayerSession.create(playerSession);
        System.out.println("Лабиринт '" + name + "' загружен.");
        System.out.println(describer.describeEnvironment(playerState));
    }
}
