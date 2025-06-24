package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.game.model.Actions;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExploreAllRoomsQuest implements GameQuest<ExploreAllRoomsState> {

    @Override
    public ExploreAllRoomsState initialize(@NotNull Maze maze, @NotNull PlayerSession session) {
        final ExploreAllRoomsState state = new ExploreAllRoomsState(maze);
        state.markVisited(session.getPlayerStateND().getPosition());
        return state;
    }

    @Override
    public QuestActionResult handleCommand(@NotNull String command, ExploreAllRoomsState state, @NotNull PlayerSession session) {
        final PlayerStateND playerState = session.getPlayerStateND();
        final Maze maze = session.getMaze();

        switch (command) {
            case Actions.GO_AHEAD:
                if (playerState.canMoveForward(maze)) {
                    playerState.moveForward();
                    Cell newCell = playerState.getPosition();
                    state.markVisited(newCell);
                    return new QuestActionResult(true, "Вы продвинулись вперёд.");
                } else {
                    return new QuestActionResult(false, "Впереди стена. Вы не можете двигаться вперёд.");
                }
            case Actions.LEFT:
                playerState.rotateCounterClockwise2D();
                return new QuestActionResult(true, "Вы повернули налево.");
            case Actions.RIGHT:
                playerState.rotateClockwise2D();
                return new QuestActionResult(true, "Вы повернули направо.");
            case Actions.BACK:
                playerState.opposite();
                return new QuestActionResult(true, "Вы развернулись назад.");
            default:
                return new QuestActionResult(false, "Неизвестная команда: " + command);
        }
    }

    @Override
    public String describeCurrentSituation(ExploreAllRoomsState state, @NotNull PlayerSession session) {
        final Cell cell = session.getPlayerStateND().getPosition();
        final Facing facing = session.getPlayerStateND().getFacing();
        final StringBuilder sb = new StringBuilder();
        sb.append("Вы находитесь в комнате ").append(cell).append(". ");
        sb.append("Смотрите в направлении: ").append(facing).append(". ");
        return sb.toString();
    }

    @Override
    public String getQuestStatus(ExploreAllRoomsState state, PlayerSession session) {
        return "Вы исследовали комнат: " + state.visitedCount() +
                ", осталось исследовать: " + state.remainingCount() + ".";
    }

    @Override
    public boolean isCompleted(ExploreAllRoomsState state, PlayerSession session) {
        return state.allVisited();
    }

    @Override
    public List<String> availableCommands(ExploreAllRoomsState state, PlayerSession session) {
        return List.of(Actions.GO_AHEAD, Actions.LEFT, Actions.RIGHT, Actions.BACK);
    }
}