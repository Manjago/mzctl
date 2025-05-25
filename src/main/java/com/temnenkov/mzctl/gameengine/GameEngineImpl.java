package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.model.EnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.Maze;

public class GameEngineImpl implements GameEngine {
    private final GameContext context;
    private final PlayerPositionProvider positionProvider;
    private final EnvironmentDescriberFactory describerFactory;

    public GameEngineImpl(GameContext context, PlayerPositionProvider positionProvider, EnvironmentDescriberFactory describerFactory) {
        this.context = context;
        this.positionProvider = positionProvider;
        this.describerFactory = describerFactory;
    }

    @Override
    public void generateMaze(String mazeName, int width, int height, MazeGeneratorFactory.Algo algo) {
        Maze maze = context.getMazeManager().generateMaze2D(width, height, algo);
        context.getMazeManager().saveMaze(mazeName, maze);
    }

    @Override
    public void loadMaze(String mazeName, String userLogin) {
        Maze maze = context.getMazeManager().loadMaze(mazeName);
        PlayerStateND playerState = positionProvider.createPlayerPosition(maze);
        EnvironmentDescriber describer = describerFactory.create(maze);
        PlayerSession playerSession = new PlayerSession(userLogin, maze, describer, playerState, null);
        context.createPlayerSession(playerSession);
    }

    @Override
    public void moveForward(String userLogin) {
        PlayerSession session = context.getPlayerSession(userLogin);
        session.getPlayerStateND().moveForward();
        context.updatePlayerSession(session);
    }

    @Override
    public void turnLeft(String userLogin) {
        PlayerSession session = context.getPlayerSession(userLogin);
        session.getPlayerStateND().rotateCounterClockwise2D();
        context.updatePlayerSession(session);
    }

    @Override
    public void turnRight(String userLogin) {
        PlayerSession session = context.getPlayerSession(userLogin);
        session.getPlayerStateND().rotateClockwise2D();
        context.updatePlayerSession(session);
    }

    @Override
    public void turnBack(String userLogin) {
        PlayerSession session = context.getPlayerSession(userLogin);
        session.getPlayerStateND().opposite();
        context.updatePlayerSession(session);
    }

    @Override
    public String describeEnvironment(String userLogin) {
        PlayerSession session = context.getPlayerSession(userLogin);
        return session.getMazeEnvironmentDescriber()
                .describeEnvironment(session.getPlayerStateND());
    }
}