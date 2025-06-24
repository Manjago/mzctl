package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.model.Maze;

import java.util.List;

public interface GameQuest<T extends QuestState> {
    /**
     * Инициализировать квест для лабиринта и игрока.
     */
    T initialize(Maze maze, PlayerSession session);

    /**
     * Обработать команду игрока (например, "идти вперёд", "повернуть налево").
     */
    QuestActionResult handleCommand(String command, T state, PlayerSession session);

    /**
     * Описать текущее окружение игрока (где он, что вокруг, доступные действия).
     */
    String describeCurrentSituation(T state, PlayerSession session);

    /**
     * Получить статус квеста (например, сколько комнат исследовано).
     */
    String getQuestStatus(T state, PlayerSession session);

    /**
     * Проверить, завершён ли квест.
     */
    boolean isCompleted(T state, PlayerSession session);

    /**
     * Получить список доступных команд.
     */
    List<String> availableCommands(T state, PlayerSession session);
}