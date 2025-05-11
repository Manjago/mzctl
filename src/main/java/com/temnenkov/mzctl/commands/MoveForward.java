package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.model.PlayerSession;
import picocli.CommandLine;

import static com.temnenkov.mzctl.commands.util.CommandUtils.loadValidPlayerSession;

@CommandLine.Command(name = "w", description = "Идти вперед")
public class MoveForward implements Runnable {

    private final GameContext context;

    public MoveForward(GameContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        final PlayerSession playerSession = loadValidPlayerSession(context);
        if (playerSession == null)
            return;
        playerSession.getPlayerStateND().moveForward();
        System.out.println(playerSession.getMazeEnvironmentDescriber().describeEnvironment(playerSession.getPlayerStateND()));
        context.updatePlayerSession(playerSession);
    }
}
