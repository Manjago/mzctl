package com.temnenkov.mzctl.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temnenkov.mzctl.auth.RoleResolver;
import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.exception.MzCtlRecoverableException;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.quest.GameQuest;
import com.temnenkov.mzctl.game.quest.QuestActionResult;
import com.temnenkov.mzctl.game.quest.QuestManager;
import com.temnenkov.mzctl.game.quest.QuestState;
import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.generation.MazeGeneratorFactory;
import com.temnenkov.mzctl.model.UserId;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelegramBotAdapter implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotAdapter.class);

    private final GameEngine gameEngine;
    private final TelegramHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TelegramBotConfig config;
    private final GameContext gameContext;
    private final RoleResolver roleResolver;

    public TelegramBotAdapter(@NotNull TelegramHttpClient client,
            @NotNull TelegramBotConfig config,
            @NotNull GameContext gameContext,
            @NotNull RoleResolver roleResolver,
            @NotNull GameEngine gameEngine) {
        this.client = client;
        this.config = config;
        this.gameContext = gameContext;
        this.roleResolver = roleResolver;
        this.gameEngine = gameEngine;
    }

    @Override
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
                logger.error("Восстановимая ошибка в Telegram-боте: {}, продолжаем работу", e.getMessage(), e);
            } catch (Exception e) { // Ловим более общие ошибки, чтобы бот не падал
                logger.error("Невосстановимая ошибка в Telegram-боте", e);
            }
        }
    }

    private void processUpdate(@NotNull JsonNode update) {
        final JsonNode message = update.get("message");
        if (message == null || !message.has("text")) {
            return;
        }

        final long chatId = message.get("chat").get("id").asLong();
        final String command = message.get("text").asText().trim();
        final UserId userId = new UserId(String.valueOf(chatId));

        try {
            PlayerSession session = getOrCreateSession(userId);

            if (session.hasActiveQuest()) {
                handleGameCommand(session, command, chatId);
            } else {
                handleLobbyCommand(session, command, chatId);
            }

            gameContext.updatePlayerSession(session);

        } catch (Exception e) {
            logger.error("Ошибка при обработке команды '{}' для пользователя {}", command, userId, e);
            try {
                sendMessage(chatId, "Произошла внутренняя ошибка. Попробуйте позже.");
            } catch (IOException ioException) {
                logger.error("Не удалось отправить сообщение об ошибке пользователю {}", userId, ioException);
            }
        }
    }

    private void handleLobbyCommand(PlayerSession session, String command, long chatId) throws IOException {
        Optional<GameQuest<?>> selectedQuestOpt = QuestManager.getQuest(command);

        if (selectedQuestOpt.isPresent()) {
            GameQuest<QuestState> quest = (GameQuest<QuestState>) selectedQuestOpt.get();

            // Инициализируем квест
            QuestState state = quest.initialize(session.getMaze(), session);
            session.setQuest(quest, state);

            String description = quest.describeCurrentSituation(state, session);
            List<String> commands = quest.availableCommands(state, session);
            sendMessageWithKeyboard(chatId, description, commands);
        } else {
            String message = "Вы находитесь в лобби. Выберите задание:\n" +
                    "/explore - Исследовать все комнаты\n" +
                    "/find_exit - Найти выход из лабиринта";
            List<String> availableQuests = new ArrayList<>(QuestManager.getAvailableQuestCommands());
            sendMessageWithKeyboard(chatId, message, availableQuests);
        }
    }

    private void handleGameCommand(@NotNull PlayerSession session, String command, long chatId) throws IOException {
        final QuestActionResult result = session.handleQuestCommand(command);

        if (session.isCurrentQuestCompleted()) {
            sendMessage(chatId, "Поздравляем! Квест выполнен!");
            session.setQuest(null, null);
            // Показываем меню лобби. Передаем "нейтральную" команду, чтобы просто показать меню.
            handleLobbyCommand(session, "/lobby", chatId);
        } else {
            String description = session.describeCurrentQuestSituation();
            // Если команда была неуспешной, добавим сообщение об ошибке к описанию
            if (!result.success() && !result.message().isEmpty()) {
                description = result.message() + "\n\n" + description;
            }
            final List<String> commands = session.getAvailableQuestCommands();
            sendMessageWithKeyboard(chatId, description, commands);
        }
    }

    private @NotNull PlayerSession getOrCreateSession(@NotNull UserId userId) {
        PlayerSession session = gameContext.getPlayerSession(userId);
        if (session == null) {
            logger.info("Сессия для пользователя {} не найдена, создаем новую.", userId);

            final String defaultMazeName = "default-maze"; // Используем уникальное имя

            // Генерируем лабиринт через GameEngine, который сохранит его для пользователя
            gameEngine.generateMaze(userId, defaultMazeName, 3, 3, MazeGeneratorFactory.Algo.RANDOMIZED_PRIM);

            // Загружаем лабиринт, что создаст и зарегистрирует сессию в GameContext
            gameEngine.loadMaze(userId, defaultMazeName);

            // Получаем только что созданную сессию
            session = gameContext.getPlayerSession(userId);

            if (session == null) {
                // Это не должно произойти, если gameEngine.loadMaze работает правильно
                throw new IllegalStateException("Не удалось создать сессию для пользователя " + userId);
            }
        }
        return session;
    }

    private void sendMessage(long chatId, String text) throws IOException {
        final String json = mapper.writeValueAsString(new Message(chatId, text));
        client.sendRequest("sendMessage", json);
    }

    private void sendMessageWithKeyboard(long chatId, String text, List<String> buttons) throws IOException {
        final String json = mapper.writeValueAsString(new MessageWithKeyboard(chatId, text, new ReplyKeyboardMarkup(buttons)));
        client.sendRequest("sendMessage", json);
    }

    // Вспомогательные классы для (де)сериализации JSON
    private record Message(@JsonProperty("chat_id") long chatId, @JsonProperty("text") String text) {}
    private record MessageWithKeyboard(@JsonProperty("chat_id") long chatId, @JsonProperty("text") String text, @JsonProperty("reply_markup") ReplyKeyboardMarkup replyMarkup) {}

    private static class ReplyKeyboardMarkup {
        @JsonProperty("keyboard")
        private final List<List<String>> keyboard;
        @JsonProperty("resize_keyboard")
        private final boolean resizeKeyboard = true;
        @JsonProperty("one_time_keyboard")
        private final boolean oneTimeKeyboard = false;

        public ReplyKeyboardMarkup(@NotNull List<String> buttons) {
            this.keyboard = buttons.stream().map(List::of).toList();
        }
    }
}