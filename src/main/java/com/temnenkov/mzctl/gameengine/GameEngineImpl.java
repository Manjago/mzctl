package com.temnenkov.mzctl.gameengine;

import com.temnenkov.mzctl.auth.Role;
import com.temnenkov.mzctl.auth.RoleResolver;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.model.EnvironmentDescriber;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;

import java.io.IOException;

public class GameEngineImpl implements GameEngine {
    private final GameContext context;
    private final PlayerPositionProvider positionProvider;
    private final EnvironmentDescriberFactory describerFactory;
    private final RoleResolver roleResolver;

    public GameEngineImpl(GameContext context,
            PlayerPositionProvider positionProvider,
            EnvironmentDescriberFactory describerFactory, RoleResolver roleResolver) {
        this.context = context;
        this.positionProvider = positionProvider;
        this.describerFactory = describerFactory;
        this.roleResolver = roleResolver;
    }

    @Override
    public void generateMaze(String userId, String mazeName, int width, int height, MazeGeneratorFactory.Algo algo) {
        Maze maze = context.getMazeManager().generateMaze2D(width, height, algo);
        try {
            context.getMazeManager().saveUserMaze(userId, mazeName, maze);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить лабиринт для пользователя " + userId, e);
        }
    }

    @Override
    public void loadMaze(String userId, String mazeName) {
        try {
            Maze maze = context.getMazeManager().loadUserMaze(userId, mazeName); // <-- Загрузка из директории пользователя!
            PlayerStateND playerState = positionProvider.createPlayerPosition(maze);
            EnvironmentDescriber describer = describerFactory.create(maze);
            PlayerSession playerSession = new PlayerSession(userId, maze, describer, playerState, roleResolver.roleByUserLogin(userId), null);
            context.createPlayerSession(playerSession);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить лабиринт '" + mazeName + "' для пользователя " + userId, e);
        }
    }

    @Override
    public void moveForward(String userLogin) {
        final PlayerSession session = context.getPlayerSession(userLogin);
        final PlayerStateND state = session.getPlayerStateND();
        if (state.canMoveForward(session.getMaze())) {
            state.moveForward();
            context.updatePlayerSession(session);
        }
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

    @Override
    public String visualizeMaze(String userLogin) {
        PlayerSession session = context.getPlayerSession(userLogin);
        if (session.getRole() == Role.PLAYER) {
            throw new SecurityException("User '" + userLogin + "' does not have permissions to visualize the maze");
        }

        final Maze maze = session.getMaze();
        final Cell position = session.getPlayerStateND().getPosition();
        final Facing facing = session.getPlayerStateND().getFacing();

        return new MazeAsciiVisualizer(maze).mazeToString(position, facing);
    }
}