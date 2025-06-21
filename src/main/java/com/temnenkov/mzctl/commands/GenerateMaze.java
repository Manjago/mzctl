package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "generate-maze", description = "Генерирует и сохраняет лабиринт")
public class GenerateMaze implements Runnable {
    @CommandLine.Option(names = {"-n", "--name"}, required = true)
    String name;
    @CommandLine.Option(names = {"-w", "--width"}, required = true)
    int width;
    @CommandLine.Option(names = {"-h", "--height"}, required = true)
    int height;
    @CommandLine.Option(names = {"-a", "--algo"}, required = true)
    MazeGeneratorFactory.Algo algo;
    @CommandLine.Option(names = {"-u", "--user"}, required = false, defaultValue = "tester")
    String userId;

    private final GameEngine gameEngine;

    public GenerateMaze(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        gameEngine.generateMaze(userId, name, width, height, algo);
        System.out.println("Лабиринт '" + name + "' создан и сохранён.");
    }
}
