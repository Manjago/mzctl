package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import picocli.CommandLine;

import static com.temnenkov.mzctl.commands.CommandUtils.loadValidPlayerSession;

@CommandLine.Command(name = "d", description = "Повернуться направо")
public class TurnRight implements Runnable {
    @Override
    public void run() {
        final PlayerSession playerSession = loadValidPlayerSession();
        if (playerSession == null)
            return;
        final PlayerStateND playerState = playerSession.getPlayerStateND();
        playerState.rotateClockwise2D();
        final MazeEnvironmentDescriber describer = playerSession.getMazeEnvironmentDescriber();
        System.out.println(describer.describeEnvironment(playerState));
        PlayerSession.update(playerSession);
    }
}
