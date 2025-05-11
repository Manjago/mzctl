package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.game.model.PlayerSession;
import picocli.CommandLine;

import static com.temnenkov.mzctl.commands.CommandUtils.loadValidPlayerSession;

@CommandLine.Command(name = "w", description = "Идти вперед")
public class MoveForward implements Runnable {
    @Override
    public void run() {
        final PlayerSession playerSession = loadValidPlayerSession();
        if (playerSession == null)
            return;
        playerSession.getPlayerStateND().moveForward();
        System.out.println(playerSession.getMazeEnvironmentDescriber().describeEnvironment(playerSession.getPlayerStateND()));
        PlayerSession.update(playerSession);
    }
}
