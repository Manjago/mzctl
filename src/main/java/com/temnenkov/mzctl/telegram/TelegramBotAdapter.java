package com.temnenkov.mzctl.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TelegramBotAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotAdapter.class);
    private static final String GO_AHEAD_CMD = "⬆️";
    private static final String LEFT_CMD = "⬅️";
    private static final String WHERE_AM_I_CMD = "❓";
    private static final String RIGHT_CMD = "➡️";
    private static final String BACK_CMD = "🔄";
    private static final String GO_AHEAD = "⬆️ Вперёд";
    private static final String LEFT = "⬅️ Влево";
    private static final String WHERE_AM_I = "❓ Где я?";
    private static final String RIGHT = "➡️ Вправо";
    private static final String BACK = "🔄 Назад";

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

    public void run() throws Exception {
        long offset = 0;
        final int longPollingTimeout = config.getLongPollingTimeout();

        while (true) {
            final String response = client.getUpdates(offset, longPollingTimeout);
            final JsonNode updates = mapper.readTree(response).get("result");

            if (updates.isArray()) {
                for (JsonNode update : updates) {
                    offset = update.get("update_id").asLong() + 1;
                    processUpdate(update);
                }
            }
        }
    }

    private void processUpdate(@NotNull JsonNode update) throws Exception {
        final JsonNode message = update.get("message");
        if (message != null && message.has("text")) {
            final long chatId = message.get("chat").get("id").asLong();
            final String text = message.get("text").asText();
            final String userId = String.valueOf(chatId);

            ensureSessionExists(userId);

            final String responseText = handleCommand(userId, text);
            sendMessageWithKeyboard(chatId, responseText);
        }
    }

    private String handleCommand(@NotNull String userId, @NotNull String command) {
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
                    gameEngine.generateMaze(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);
                    yield "Лабиринт '" + args[1] + "' сгенерирован";
                } else {
                    yield "Использование: /generate <имя> <ширина> <высота>";
                }
            }
            case "/load" -> {
                if (args.length == 2) {
                    gameEngine.loadMaze(args[1], userId);
                    yield "Лабиринт '" + args[1] + "' загружен.\n" + gameEngine.describeEnvironment(userId);
                } else {
                    yield "Использование: /load <имя>";
                }
            }
            default -> "Неизвестная команда: '" + command + "'. Введите /help для получения списка команд.";
        };
    }

    private void sendMessage(long chatId, String text) throws IOException, InterruptedException {
        final String json = mapper.writeValueAsString(new Message(chatId, text));
        client.sendRequest("sendMessage", json);
    }

    private record Message(
            @JsonProperty("chat_id") long chatId,
            @JsonProperty("text") String text
    ) {}

    private void ensureSessionExists(String userId) {
        PlayerSession session = gameContext.getPlayerSession(userId);
        if (session == null) {
            String defaultMazeName = "default";
            try {
                gameEngine.loadMaze(defaultMazeName, userId);
            } catch (Exception e) {
                logger.warn("Лабиринт '{}' не найден. Генерируем новый лабиринт автоматически.", defaultMazeName);
                gameEngine.generateMaze(defaultMazeName, 3, 3, MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);
                gameEngine.loadMaze(defaultMazeName, userId);
            }
        }
    }

    private void sendMessageWithKeyboard(long chatId, String text) throws IOException, InterruptedException {
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
                {LEFT, WHERE_AM_I, RIGHT},
                {BACK}
        };

        @JsonProperty("resize_keyboard")
        private final boolean resizeKeyboard = true;

        @JsonProperty("one_time_keyboard")
        private final boolean oneTimeKeyboard = false;
    }

}