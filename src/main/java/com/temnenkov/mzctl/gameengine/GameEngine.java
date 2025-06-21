package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.UserId;

public interface GameEngine {
    void generateMaze(UserId userId, String mazeName, int width, int height, MazeGeneratorFactory.Algo algo);
    void loadMaze(UserId userId, String mazeName);
    void moveForward(UserId userId);
    void turnLeft(UserId userId);
    void turnRight(UserId userId);
    void turnBack(UserId userId);
    String describeEnvironment(UserId userId);
    String visualizeMaze(UserId userId);
}
