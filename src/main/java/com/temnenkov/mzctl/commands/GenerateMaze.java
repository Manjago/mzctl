package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.Maze;
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

    @Override
    public void run() {
       final MazeManager mazeManager = MazeManager.getInstance();
       final Maze maze = mazeManager.generateMaze2D(width, height, algo);
       mazeManager.saveMaze(name, maze);
       System.out.println("Лабиринт '" + name + "' создан и сохранён.");
    }
}
