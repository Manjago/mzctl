package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;

public interface GameEngine {
    void generateMaze(String mazeName, int width, int height, MazeGeneratorFactory.Algo algo);
    void loadMaze(String mazeName, String userLogin);
    void moveForward(String userLogin);
    void turnLeft(String userLogin);
    void turnRight(String userLogin);
    void turnBack(String userLogin);
    String describeEnvironment(String userLogin);
    GameContext getContext();
}
