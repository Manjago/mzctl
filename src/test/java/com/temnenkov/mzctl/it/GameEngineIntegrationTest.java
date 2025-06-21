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
import com.temnenkov.mzctl.model.UserId;
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
    private UserId userLogin;

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

        userLogin = new UserId("tester");
    }

    @Test
    @DisplayName("Должен успешно загружать лабиринт, если файл существует")
    void shouldLoadMazeSuccessfullyWhenMazeExists() {
        assertDoesNotThrow(() -> gameEngine.loadMaze(userLogin, "test2"), "Загрузка существующего лабиринта не должна" +
                " бросать исключений");
    }

    @Test
    @DisplayName("Должен бросать исключение при загрузке несуществующего лабиринта")
    void shouldThrowExceptionWhenMazeNotExists() {
        final MazeSerializationException exception = assertThrows(MazeSerializationException.class,
                () -> gameEngine.loadMaze(userLogin, "test-not-existed"));
        assertTrue(exception.getMessage().contains("Cannot read maze from file"), "Сообщение исключения должно " +
                "содержать информацию о невозможности прочитать файл лабиринта");
    }

    @Test
    @DisplayName("Игрок должен корректно двигаться вперёд и получать правильное описание окружения")
    void shouldMoveForwardAndDescribeEnvironmentCorrectly() {
        // загружаем лабиринт
        gameEngine.loadMaze(userLogin, "test2");

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

    @Test
    @DisplayName("Игрок должен корректно повернуться налево и получать правильное описание окружения")
    void shouldTurnLeftAndDescribeEnvironmentCorrectly() {
        // загружаем лабиринт
        gameEngine.loadMaze(userLogin, "test2");

        // для наглядности покажем его
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверяем начальное окружение
        String initialDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:X R:. B:#", initialDescription, "Некорректное начальное описание окружения");

        // поворачиваемся налево
        gameEngine.turnLeft(userLogin);

        // для наглядности еще раз покажем
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверка
        String afterTurnRightDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:# R:X B:.", afterTurnRightDescription, "Некорректное описание окружения после поворота налево");

    }

    @Test
    @DisplayName("Игрок должен корректно повернуться назад и получать правильное описание окружения")
    void shouldTurnBackAndDescribeEnvironmentCorrectly() {
        // загружаем лабиринт
        gameEngine.loadMaze(userLogin, "test2");

        // для наглядности покажем его
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверяем начальное окружение
        String initialDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:X R:. B:#", initialDescription, "Некорректное начальное описание окружения");

        // поворачиваемся назад
        gameEngine.turnBack(userLogin);

        // для наглядности еще раз покажем
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверка
        String afterTurnRightDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:# L:. R:X B:X", afterTurnRightDescription, "Некорректное описание окружения после поворота назад");

    }

    @Test
    @DisplayName("Игрок должен попытаться выйти за пределы лабиринта и получать правильное описание окружения")
    void shouldGoAwayAndDescribeEnvironmentCorrectly() {
        // загружаем лабиринт
        gameEngine.loadMaze(userLogin, "test2");

        // для наглядности покажем его
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверяем начальное окружение
        String initialDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:X R:. B:#", initialDescription, "Некорректное начальное описание окружения");

        // идем вперед
        gameEngine.moveForward(userLogin);

        // для наглядности еще раз покажем
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверка - остались на месте
        String afterMoveDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:X R:. B:#", afterMoveDescription, "Некорректное описание окружения после попытки выхода за пределы лабиринта");
    }

    @Test
    @DisplayName("Игрок должен попытаться пройти сквозь стену и получать правильное описание окружения")
    void shouldGoThroughWallAndDescribeEnvironmentCorrectly() {
        // загружаем лабиринт
        gameEngine.loadMaze(userLogin, "test2");

        // для наглядности покажем его
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверяем начальное окружение
        String initialDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:X L:X R:. B:#", initialDescription, "Некорректное начальное описание окружения");

        // поворачиваемся назад
        gameEngine.turnBack(userLogin);

        // для наглядности еще раз покажем
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверка
        String afterTurnBackDescription = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:# L:. R:X B:X", afterTurnBackDescription, "Некорректное описание окружения после поворота назад");

        // идем вперед (пытаемся пройти сквозь стену)
        gameEngine.moveForward(userLogin);

        // для наглядности еще раз покажем
        System.out.println(gameEngine.visualizeMaze(userLogin));

        // проверка - остались на месте
        String afterGo = gameEngine.describeEnvironment(userLogin);
        assertEquals("F:# L:. R:X B:X", afterGo, "Некорректное описание окружения после попытки пройти сквозь стену");

    }
}