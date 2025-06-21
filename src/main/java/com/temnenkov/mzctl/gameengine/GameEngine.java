package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.generation.MazeGeneratorFactory;

public interface GameEngine {
    void generateMaze(String userId, String mazeName, int width, int height, MazeGeneratorFactory.Algo algo);
    void loadMaze(String userId, String mazeName);
    void moveForward(String userLogin);
    void turnLeft(String userLogin);
    void turnRight(String userLogin);
    void turnBack(String userLogin);
    String describeEnvironment(String userLogin);
    String visualizeMaze(String userLogin);
}
