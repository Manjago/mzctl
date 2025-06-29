package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.model.Cell;
import org.jetbrains.annotations.NotNull;

public class FindExitQuestState implements QuestState {
    @NotNull
    private final Cell exitCoordinates;

    public FindExitQuestState(@NotNull Cell exitCoordinates) {
        this.exitCoordinates = exitCoordinates;
    }

    public @NotNull Cell getExitCoordinates() {
        return exitCoordinates;
    }
}