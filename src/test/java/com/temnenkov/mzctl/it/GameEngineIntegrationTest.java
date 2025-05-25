package com.temnenkov.mzctl.it;

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineIntegrationTest {

    private static final Path TEST_RESOURCES_PATH = Path.of("src/test/resources");

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
        FixedPlayerPositionProvider positionProvider = new FixedPlayerPositionProvider(0, 0, Facing.NORTH);
        container.registerBean(PlayerPositionProvider.class, positionProvider);

        // используем короткую фабрику для тестов
        container.registerBean(EnvironmentDescriberFactory.class, new ShortDescriberFactory());

        // Создаем GameEngine через контейнер
        gameEngine = container.createBean(GameEngineImpl.class);

        userLogin = "tester";
    }

    @Test
    @DisplayName("Должен успешно загружать лабиринт, если файл существует")
    void shouldLoadMazeSuccessfullyWhenMazeExists() {
        assertDoesNotThrow(() -> gameEngine.loadMaze("test2", userLogin),
                "Загрузка существующего лабиринта не должна бросать исключений");
    }

    @Test
    @DisplayName("Должен бросать исключение при загрузке несуществующего лабиринта")
    void shouldThrowExceptionWhenMazeNotExists() {
        final MazeSerializationException exception = assertThrows(MazeSerializationException.class,
                () -> gameEngine.loadMaze("test-not-existed", userLogin));
        assertTrue(exception.getMessage().contains("Cannot read maze from file"),
                "Сообщение исключения должно содержать информацию о невозможности прочитать файл лабиринта");
    }
}