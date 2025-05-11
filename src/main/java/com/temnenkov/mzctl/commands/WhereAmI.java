package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import picocli.CommandLine;

import static com.temnenkov.mzctl.commands.util.CommandUtils.loadValidPlayerSession;

@CommandLine.Command(name = "?", description = "Повторить описание окружения")
public class WhereAmI implements Runnable {

    private final GameContext context;

    public WhereAmI(GameContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        final PlayerSession playerSession = loadValidPlayerSession(context);
        if (playerSession == null)
            return;
        final PlayerStateND playerState = playerSession.getPlayerStateND();
        final MazeEnvironmentDescriber describer = playerSession.getMazeEnvironmentDescriber();
        System.out.println(describer.describeEnvironment(playerState));
    }
}
