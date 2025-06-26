package com.temnenkov.mzctl.game.quest;

import com.temnenkov.mzctl.auth.Role;
import com.temnenkov.mzctl.game.model.Actions;
import com.temnenkov.mzctl.game.model.EnvironmentDescriber;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExploreAllRoomsQuestTest {

    private PlayerSession session;
    private ExploreAllRoomsQuest quest;
    private ExploreAllRoomsState state;

    @BeforeEach
    void setup() {
        final Maze maze = MazeFactory.createFullConnectedMaze(MazeDim.of(2, 2)); // 2x2 лабиринт
        PlayerStateND playerState = new PlayerStateND(Cell.of(0, 0), Facing.NORTH);
        EnvironmentDescriber describer = new MazeEnvironmentDescriber(maze);
        session = new PlayerSession("test", maze, describer, playerState, Role.PLAYER, null);

        quest = new ExploreAllRoomsQuest();
        state = quest.initialize(maze, session);
    }

    @Test
    void testInitialConditions() {
        assertFalse(quest.isCompleted(state, session));
        assertEquals("Вы исследовали комнат: 1, осталось исследовать: 3.", quest.getQuestStatus(state, session));
    }

    @Test
    void testAvailableCommands() {
        List<String> commands = quest.availableCommands(state, session);
        assertTrue(commands.containsAll(List.of(Actions.GO_AHEAD, Actions.LEFT, Actions.RIGHT, Actions.BACK)));
    }

    @Test
    void testMoveForwardSuccess() {
        session.getPlayerStateND().opposite();
        QuestActionResult result = quest.handleCommand(Actions.GO_AHEAD, state, session);
        assertTrue(result.success());
        assertEquals("Вы продвинулись вперёд.", result.message());

        assertEquals(2, state.visitedCount());
        assertEquals(Cell.of(1, 0), session.getPlayerStateND().getPosition());
    }

    @Test
    void testMoveForwardBlockedByWall() {
        session.getPlayerStateND().rotateClockwise2D(); // повернулись направо (на восток)
        session.getPlayerStateND().moveForward(); // перешли в (1,0)
        session.getPlayerStateND().rotateClockwise2D(); // смотрим на юг
        session.getPlayerStateND().moveForward(); // (1,1)
        session.getPlayerStateND().rotateClockwise2D(); // смотрим на запад
        session.getPlayerStateND().moveForward(); // (0,1)
        session.getPlayerStateND().rotateClockwise2D(); // смотрим на север (вверх)
        session.getPlayerStateND().moveForward(); // (0,0)

        QuestActionResult result = quest.handleCommand(Actions.GO_AHEAD, state, session);
        assertFalse(result.success());
        assertEquals("Впереди стена. Вы не можете двигаться вперёд.", result.message());
    }

    @Test
    void testQuestCompletion() {
        // обходим все комнаты лабиринта
        state.markVisited(Cell.of(0, 0));
        state.markVisited(Cell.of(0, 1));
        state.markVisited(Cell.of(1, 0));
        state.markVisited(Cell.of(1, 1));

        assertTrue(quest.isCompleted(state, session));
        assertEquals("Вы исследовали комнат: 4, осталось исследовать: 0.", quest.getQuestStatus(state, session));
    }

    @Test
    void testDescribeCurrentSituation() {
        String description = quest.describeCurrentSituation(state, session);
        assertTrue(description.contains("Вы находитесь в комнате Cell[0, 0]"));
        assertTrue(description.contains("Смотрите в направлении: Facing[Y(-)]."));
    }

    @Test
    void testHandleUnknownCommand() {
        QuestActionResult result = quest.handleCommand("неизвестная команда", state, session);
        assertFalse(result.success());
        assertEquals("Неизвестная команда: неизвестная команда", result.message());
    }
}