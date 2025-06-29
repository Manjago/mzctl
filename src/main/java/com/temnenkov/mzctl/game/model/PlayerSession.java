package com.temnenkov.mzctl.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.temnenkov.mzctl.auth.Role;
import com.temnenkov.mzctl.game.quest.GameQuest;
import com.temnenkov.mzctl.game.quest.QuestActionResult;
import com.temnenkov.mzctl.game.quest.QuestState;
import com.temnenkov.mzctl.gameengine.PlayerPositionProvider;
import com.temnenkov.mzctl.gameengine.RandomPlayerPositionProvider;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.UserId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerSession {
    private final String login;
    private final Maze maze;
    private final EnvironmentDescriber mazeEnvironmentDescriber;
    private final PlayerStateND playerStateND;
    private final Role role;
    private Long version;
    private GameQuest<?> currentQuest;
    private QuestState currentQuestState;

    //todo убрать эту аннотацию, мы же перешли на kryo с messagePack
    @JsonCreator
    public PlayerSession(@NotNull String login,
            @NotNull Maze maze,
            @NotNull EnvironmentDescriber environmentDescriber,
            @NotNull PlayerStateND playerStateND,
            @NotNull Role role,
            @Nullable Long version) {
        this.login = login;
        this.maze = maze;
        this.mazeEnvironmentDescriber = environmentDescriber;
        this.playerStateND = playerStateND;
        this.role = role;
        this.version = version;
    }

    // Добавляем этот новый конструктор
    public PlayerSession(@NotNull String login, @NotNull Maze maze, @NotNull Role role) {
        this.login = login;
        this.maze = maze;
        this.role = role;
        this.mazeEnvironmentDescriber = new MazeEnvironmentDescriber(maze);
        final PlayerPositionProvider positionProvider = new RandomPlayerPositionProvider(ThreadLocalRandom::current);
        this.playerStateND = positionProvider.createPlayerPosition(maze);
        this.version = 0L;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getLogin() {
        return login;
    }

    public Maze getMaze() {
        return maze;
    }

    public EnvironmentDescriber getMazeEnvironmentDescriber() {
        return mazeEnvironmentDescriber;
    }

    public PlayerStateND getPlayerStateND() {
        return playerStateND;
    }

    public Role getRole() {
        return role;
    }

    public UserId getUserId() {
        return new UserId(login);
    }

    @SuppressWarnings("unchecked")
    public <T extends QuestState> T getQuestState() {
        return (T) currentQuestState;
    }

    public GameQuest<?> getCurrentQuest() {
        return currentQuest;
    }

    public <T extends QuestState> void setQuest(GameQuest<T> quest, T state) {
        this.currentQuest = quest;
        this.currentQuestState = state;
    }

    public boolean hasActiveQuest() {
        return currentQuest != null;
    }

    @SuppressWarnings("unchecked")
    public QuestActionResult handleQuestCommand(String command) {
        if (!hasActiveQuest()) {
            return new QuestActionResult(false, "Нет активного квеста.");
        }
        final GameQuest<QuestState> quest = (GameQuest<QuestState>) this.currentQuest;
        final QuestState state = this.getQuestState();
        return quest.handleCommand(command, state, this);
    }

    @SuppressWarnings("unchecked")
    public boolean isCurrentQuestCompleted() {
        if (!hasActiveQuest()) {
            return false;
        }
        final GameQuest<QuestState> quest = (GameQuest<QuestState>) this.currentQuest;
        final QuestState state = this.getQuestState();
        return quest.isCompleted(state, this);
    }

    @SuppressWarnings("unchecked")
    public String describeCurrentQuestSituation() {
        if (!hasActiveQuest()) {
            return "Нет активного квеста.";
        }
        final GameQuest<QuestState> quest = (GameQuest<QuestState>) this.currentQuest;
        final QuestState state = this.getQuestState();
        return quest.describeCurrentSituation(state, this);
    }

    @SuppressWarnings("unchecked")
    public List<String> getAvailableQuestCommands() {
        if (!hasActiveQuest()) {
            return List.of();
        }
        final GameQuest<QuestState> quest = (GameQuest<QuestState>) this.currentQuest;
        final QuestState state = this.getQuestState();
        return quest.availableCommands(state, this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        PlayerSession that = (PlayerSession) o;
        return login.equals(that.login) && maze.equals(that.maze) && mazeEnvironmentDescriber.equals(that.mazeEnvironmentDescriber) && playerStateND.equals(that.playerStateND) && role == that.role && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        int result = login.hashCode();
        result = 31 * result + maze.hashCode();
        result = 31 * result + mazeEnvironmentDescriber.hashCode();
        result = 31 * result + playerStateND.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + Objects.hashCode(version);
        return result;
    }

    @Override
    public String toString() {
        return "PlayerSession{" + "login='" + login + '\'' + ", maze=" + maze.toShortString() + ", mazeEnvironmentDescriber=" + mazeEnvironmentDescriber + ", playerStateND=" + playerStateND + ", role=" + role + ", version=" + version + '}';
    }
}
