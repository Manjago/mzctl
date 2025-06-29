package com.temnenkov.mzctl.game.quest;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuestManager {

    private static final Map<String, GameQuest<?>> QUESTS = new ConcurrentHashMap<>();

    static {
        // Регистрируем все доступные квесты в игре.
        // Ключ - это команда, которую вводит пользователь для старта.
        //todo подумать про введение enum
        QUESTS.put("/explore", new ExploreAllRoomsQuest());
        QUESTS.put("/find_exit", new FindExitQuest());
    }

    public static @NotNull Optional<GameQuest<?>> getQuest(String command) {
        return Optional.ofNullable(QUESTS.get(command));
    }

    @Contract(pure = true)
    public static @NotNull Set<String> getAvailableQuestCommands() {
        return QUESTS.keySet();
    }
}