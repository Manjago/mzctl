package com.temnenkov.mzctl.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.exception.MzCtlRecoverableException;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.UserId;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.temnenkov.mzctl.game.model.Actions.BACK;
import static com.temnenkov.mzctl.game.model.Actions.GO_AHEAD;
import static com.temnenkov.mzctl.game.model.Actions.LEFT;
import static com.temnenkov.mzctl.game.model.Actions.RIGHT;
import static com.temnenkov.mzctl.game.model.Actions.WHERE_AM_I;

public class TelegramBotAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotAdapter.class);
    private static final String GO_AHEAD_CMD = "⬆️";
    private static final String LEFT_CMD = "⬅️";
    private static final String WHERE_AM_I_CMD = "❓";
    private static final String RIGHT_CMD = "➡️";
    private static final String BACK_CMD = "🔄";

    private final TelegramHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameEngine gameEngine;
    private final TelegramBotConfig config;
    private final GameContext gameContext;

    public TelegramBotAdapter(@NotNull TelegramHttpClient client,
            @NotNull GameEngine gameEngine,
            @NotNull TelegramBotConfig config,
            @NotNull GameContext gameContext) {
        this.client = client;
        this.gameEngine = gameEngine;
        this.config = config;
        this.gameContext = gameContext;
    }

    public void run() {
        long offset = 0;
        final int longPollingTimeout = config.getLongPollingTimeout();

        while (true) {
            try {
                final String response = client.getUpdates(offset, longPollingTimeout);
                final JsonNode updates = mapper.readTree(response).get("result");

                if (updates.isArray()) {
                    for (JsonNode update : updates) {
                        offset = update.get("update_id").asLong() + 1;
                        processUpdate(update);
                    }
                }
            } catch (MzCtlRecoverableException e) {
                logger.error("Ошибка при работе Telegram-бота: {}, продолжаем работу", e.getMessage(), e);
            }
            catch (IOException e) {
                logger.warn("Telegram bot request failed", e);
            }
        }
    }

    private void processUpdate(@NotNull JsonNode update) throws IOException {
        final JsonNode message = update.get("message");
        if (message != null && message.has("text")) {
            final long chatId = message.get("chat").get("id").asLong();
            final String text = message.get("text").asText();
            final UserId userId = new UserId(String.valueOf(chatId));

            ensureSessionExists(userId);

            final String responseText = handleCommand(userId, text);
            sendMessageWithKeyboard(chatId, responseText);
        }
    }

    private String handleCommand(@NotNull UserId userId, @NotNull String command) {
        final String[] args = command.split("\\s+");
        logger.debug("handleCommand command = '{}'", args[0]);
        return switch (args[0]) {
            case "/start" -> "Добро пожаловать! Используйте кнопки для движения или введите /help для получения списка команд.";
            case "/help" -> """
            Доступные команды:
            ⬆️ Вперёд – движение вперёд
            ⬅️ Влево – поворот налево
            ➡️ Вправо – поворот направо
            🔄 Назад – поворот назад
            ❓ Где я? – повторить описание текущего окружения
            /generate <имя> <ширина> <высота> – создать новый лабиринт
            /load <имя> – загрузить существующий лабиринт
            """;
            case GO_AHEAD_CMD -> { gameEngine.moveForward(userId); yield gameEngine.describeEnvironment(userId); }
            case LEFT_CMD -> { gameEngine.turnLeft(userId); yield gameEngine.describeEnvironment(userId); }
            case RIGHT_CMD -> { gameEngine.turnRight(userId); yield gameEngine.describeEnvironment(userId); }
            case BACK_CMD -> { gameEngine.turnBack(userId); yield gameEngine.describeEnvironment(userId); }
            case WHERE_AM_I_CMD -> gameEngine.describeEnvironment(userId);
            case "/generate" -> {
                if (args.length == 4) {
                    gameEngine.generateMaze(userId, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);
                    yield "Лабиринт '" + args[1] + "' сгенерирован";
                } else {
                    yield "Использование: /generate <имя> <ширина> <длина>";
                }
            }
            case "/load" -> {
                if (args.length == 2) {
                    gameEngine.loadMaze(userId, args[1]);
                    yield "Лабиринт '" + args[1] + "' загружен.\n" + gameEngine.describeEnvironment(userId);
                } else {
                    yield "Использование: /load <имя>";
                }
            }
            default -> "Неизвестная команда: '" + command + "'. Введите /help для получения списка команд.";
        };
    }

    private void sendMessage(long chatId, String text) throws IOException {
        final String json = mapper.writeValueAsString(new Message(chatId, text));
        client.sendRequest("sendMessage", json);
    }

    private record Message(
            @JsonProperty("chat_id") long chatId,
            @JsonProperty("text") String text
    ) {}

    private void ensureSessionExists(@NotNull UserId userId) {
        final PlayerSession session = gameContext.getPlayerSession(userId);
        if (session == null) {
            final String defaultMazeName = "default";
            try {
                // Проверяем, есть ли лабиринт default у пользователя
                gameContext.getMazeManager().loadUserMaze(userId, defaultMazeName);
                gameEngine.loadMaze(userId, defaultMazeName);
            } catch (Exception e) {
                logger.warn("Лабиринт '{}' не найден для пользователя {}. Генерируем новый лабиринт автоматически.", defaultMazeName, userId);
                // Если нет, генерируем и сохраняем лабиринт персонально для пользователя
                gameEngine.generateMaze(userId, defaultMazeName, 3, 3, MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);
                // Теперь загружаем его для пользователя
                gameEngine.loadMaze(userId, defaultMazeName); // здесь тоже правильно
            }
        }
    }

    private void sendMessageWithKeyboard(long chatId, String text) throws IOException {
        final String json = mapper.writeValueAsString(new MessageWithKeyboard(chatId, text, new ReplyKeyboardMarkup()));
        client.sendRequest("sendMessage", json);
    }

    private record MessageWithKeyboard(
            @JsonProperty("chat_id") long chatId,
            @JsonProperty("text") String text,
            @JsonProperty("reply_markup") ReplyKeyboardMarkup replyMarkup
    ) {}

    private static class ReplyKeyboardMarkup {
        @JsonProperty("keyboard")
        private final String[][] keyboard = {
                {GO_AHEAD},
                {LEFT, RIGHT},
                {BACK},
                {WHERE_AM_I}
        };

        @JsonProperty("resize_keyboard")
        private final boolean resizeKeyboard = true;

        @JsonProperty("one_time_keyboard")
        private final boolean oneTimeKeyboard = false;
    }

}