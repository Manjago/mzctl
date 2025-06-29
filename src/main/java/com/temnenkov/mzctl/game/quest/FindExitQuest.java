package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.game.model.Actions;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FindExitQuest implements GameQuest<FindExitQuestState> {

    @Override
    public FindExitQuestState initialize(@NotNull Maze maze, @NotNull PlayerSession session) {
        // Выбираем случайную точку для выхода, которая не является стартовой
        final Random random = ThreadLocalRandom.current();
        Cell exit;
        do {
            exit = maze.getRandomCell(random);
        } while (exit.equals(session.getPlayerStateND().getPosition()));

        return new FindExitQuestState(exit);
    }

    @Override
    public QuestActionResult handleCommand(@NotNull String command, @NotNull FindExitQuestState state, @NotNull PlayerSession session) {
        // Логика движения полностью аналогична ExploreAllRoomsQuest.
        // Это говорит о том, что ее можно todo будет вынести в общий класс или метод.
        final PlayerStateND playerState = session.getPlayerStateND();
        final Maze maze = session.getMaze();

        switch (command) {
            case Actions.GO_AHEAD:
                if (playerState.canMoveForward(maze)) {
                    playerState.moveForward();
                    return new QuestActionResult(true, "Вы продвинулись вперёд.");
                } else {
                    return new QuestActionResult(false, "Впереди стена или граница лабиринта.");
                }
            case Actions.LEFT:
                playerState.rotateCounterClockwise2D();
                return new QuestActionResult(true, "Вы повернули налево.");
            case Actions.RIGHT:
                playerState.rotateClockwise2D();
                return new QuestActionResult(true, "Вы повернули направо.");
            case Actions.BACK:
                playerState.opposite();
                return new QuestActionResult(true, "Вы развернулись.");
            case Actions.WHERE_AM_I:
                // Команда не меняет состояние, просто возвращаем успех.
                // Описание будет отправлено в основном цикле.
                return new QuestActionResult(true, "");
            default:
                return new QuestActionResult(false, "Неизвестная команда: " + command);
        }
    }

    @Override
    public String describeCurrentSituation(@NotNull FindExitQuestState state, @NotNull PlayerSession session) {
        final String environment = session.getMazeEnvironmentDescriber().describeEnvironment(session.getPlayerStateND());
        return environment + "\n" + getQuestStatus(state, session);
    }

    @Override
    public String getQuestStatus(@NotNull FindExitQuestState state, @NotNull PlayerSession session) {
        return "Ваша цель — найти выход. Он где-то в лабиринте...";
    }

    @Override
    public boolean isCompleted(@NotNull FindExitQuestState state, @NotNull PlayerSession session) {
        return session.getPlayerStateND().getPosition().equals(state.getExitCoordinates());
    }

    @Override
    public List<String> availableCommands(@NotNull FindExitQuestState state, @NotNull PlayerSession session) {
        // Возвращаем стандартный набор команд.
        return List.of(Actions.GO_AHEAD, Actions.LEFT, Actions.RIGHT, Actions.BACK, Actions.WHERE_AM_I);
    }
}