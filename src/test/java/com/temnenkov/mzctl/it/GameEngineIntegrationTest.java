package com.temnenkov.mzctl.it;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.context.SimpleGameContext;
import com.temnenkov.mzctl.game.MazeManager;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.gameengine.FixedPlayerPositionProvider;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.gameengine.GameEngineImpl;
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

    @BeforeEach
    void setUp() throws IOException {
        final MazeManager mazeManager = new MazeManager(TEST_RESOURCES_PATH);
        final GameContext gameContext = new SimpleGameContext(mazeManager);
        gameEngine = new GameEngineImpl(gameContext, new FixedPlayerPositionProvider(0, 0, Facing.NORTH));
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