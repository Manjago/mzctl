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
import com.temnenkov.mzctl.model.UserId;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import org.jetbrains.annotations.NotNull;

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
    public void generateMaze(@NotNull UserId userId, String mazeName, int width, int height, MazeGeneratorFactory.Algo algo) {
        Maze maze = context.getMazeManager().generateMaze2D(width, height, algo);
        try {
            context.getMazeManager().saveUserMaze(userId, mazeName, maze);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить лабиринт для пользователя " + userId, e);
        }
    }

    @Override
    public void loadMaze(@NotNull UserId userId, String mazeName) {
        try {
            Maze maze = context.getMazeManager().loadUserMaze(userId, mazeName);
            PlayerStateND playerState = positionProvider.createPlayerPosition(maze);
            EnvironmentDescriber describer = describerFactory.create(maze);
            PlayerSession playerSession = new PlayerSession(userId.getValue(), maze, describer, playerState, roleResolver.roleByUserLogin(userId), null);
            context.createPlayerSession(playerSession);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить лабиринт '" + mazeName + "' для пользователя " + userId, e);
        }
    }

    @Override
    public void moveForward(@NotNull UserId userId) {
        final PlayerSession session = context.getPlayerSession(userId);
        final PlayerStateND state = session.getPlayerStateND();
        if (state.canMoveForward(session.getMaze())) {
            state.moveForward();
            context.updatePlayerSession(session);
        }
    }

    @Override
    public void turnLeft(@NotNull UserId userId) {
        PlayerSession session = context.getPlayerSession(userId);
        session.getPlayerStateND().rotateCounterClockwise2D();
        context.updatePlayerSession(session);
    }

    @Override
    public void turnRight(@NotNull UserId userId) {
        PlayerSession session = context.getPlayerSession(userId);
        session.getPlayerStateND().rotateClockwise2D();
        context.updatePlayerSession(session);
    }

    @Override
    public void turnBack(@NotNull UserId userId) {
        PlayerSession session = context.getPlayerSession(userId);
        session.getPlayerStateND().opposite();
        context.updatePlayerSession(session);
    }

    @Override
    public String describeEnvironment(@NotNull UserId userId) {
        PlayerSession session = context.getPlayerSession(userId);
        return session.getMazeEnvironmentDescriber()
                .describeEnvironment(session.getPlayerStateND());
    }

    @Override
    public String visualizeMaze(@NotNull UserId userId) {
        PlayerSession session = context.getPlayerSession(userId);
        if (session.getRole() == Role.PLAYER) {
            throw new SecurityException("User '" + userId + "' does not have permissions to visualize the maze");
        }

        final Maze maze = session.getMaze();
        final Cell position = session.getPlayerStateND().getPosition();
        final Facing facing = session.getPlayerStateND().getFacing();

        return new MazeAsciiVisualizer(maze).mazeToString(position, facing);
    }
}