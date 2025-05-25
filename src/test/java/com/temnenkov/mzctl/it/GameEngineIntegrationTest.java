package com.temnenkov.mzctl.it;

import com.temnenkov.mzctl.auth.RoleResolver;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.context.SimpleGameContext;
import com.temnenkov.mzctl.di.SimpleDIContainer;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.ShortDescriberFactory;
import com.temnenkov.mzctl.gameengine.EnvironmentDescriberFactory;
import com.temnenkov.mzctl.gameengine.FixedPlayerPositionProvider;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.gameengine.GameEngineImpl;
import com.temnenkov.mzctl.gameengine.PlayerPositionProvider;
import com.temnenkov.mzctl.model.serialize.MazeSerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineIntegrationTest {

    private static final Path TEST_RESOURCES_PATH = Path.of("src/test/resources");
    private static final int START_ROW = 0;
    private static final int START_COLUMN = 0;
    private static final Facing START_FACING = Facing.NORTH;

    private GameEngine gameEngine;
    private String userLogin;

    private SimpleDIContainer container;

    @BeforeEach
    void setUp() throws IOException {
        container = new SimpleDIContainer();

        // Регистрируем MazeManager
        MazeManager mazeManager = new MazeManager(TEST_RESOURCES_PATH);
        container.registerBean(MazeManager.class, mazeManager);

        // Регистрируем GameContext
        GameContext gameContext = new SimpleGameContext(mazeManager);
        container.registerBean(GameContext.class, gameContext);

        // Регистрируем PlayerPositionProvider (фиксированный для тестов)
        FixedPlayerPositionProvider positionProvider = new FixedPlayerPositionProvider(START_ROW, START_COLUMN,
                START_FACING);
        container.registerBean(PlayerPositionProvider.class, positionProvider);

        // используем короткую фабрику для тестов
        container.registerBean(EnvironmentDescriberFactory.class, new ShortDescriberFactory());

        container.registerBean(RoleResolver.class, new RoleResolver());

        // Создаем GameEngine через контейнер
        gameEngine = container.createBean(GameEngineImpl.class);

        userLogin = "tester";
    }

    @Test
    @DisplayName("Должен успешно загружать лабиринт, если файл существует")
    void shouldLoadMazeSuccessfullyWhenMazeExists() {
        assertDoesNotThrow(() -> gameEngine.loadMaze("test2", userLogin), "Загрузка существующего лабиринта не должна" +
                " бросать исключений");
    }

    @Test
    @DisplayName("Должен бросать исключение при загрузке несуществующего лабиринта")
    void shouldThrowExceptionWhenMazeNotExists() {
        final MazeSerializationException exception = assertThrows(MazeSerializationException.class,
                () -> gameEngine.loadMaze("test-not-existed", userLogin));
        assertTrue(exception.getMessage().contains("Cannot read maze from file"), "Сообщение исключения должно " +
                "содержать информацию о невозможности прочитать файл лабиринта");
    }

    @Test
    @DisplayName("Игрок должен корректно двигаться вперёд и получать правильное описание окружения")
    void shouldMoveForwardAndDescribeEnvironmentCorrectly() {
        // загружаем лабиринт
        gameEngine.loadMaze("test2", userLogin);

        // для наглядности покажем его
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверяем начальное окружение
        String initialDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:X R:. B:#", initialDescription, "Некорректное начальное описание окружения");

        // нам нужно место для шага вперед - поворачиваемся направо
        gameEngine.turnRight(userLogin);
        String afterTurnRightDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:. L:X R:# B:X", afterTurnRightDescription, "Некорректное описание окружения после поворота направо");

        // двигаем игрока вперёд
        gameEngine.moveForward(userLogin);

        // проверяем окружение после движения
        String afterMoveDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:. L:X R:# B:.", afterMoveDescription, "Некорректное описание окружения после движения вперёд");
        System.out.println(gameEngine.visualizeMaze(userLogin));
    }
}