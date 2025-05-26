package com.temnenkov.mzctl.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.temnenkov.mzctl.gameengine.GameEngine;
import org.jetbrains.annotations.NotNull;

public class TelegramBotAdapter {
    private final TelegramHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameEngine gameEngine;
    private final TelegramBotConfig config;

    public TelegramBotAdapter(@NotNull TelegramHttpClient client, @NotNull GameEngine gameEngine, @NotNull TelegramBotConfig config) {
        this.client = client;
        this.gameEngine = gameEngine;
        this.config = config;
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

            final String responseText = handleCommand(userId, text);
            sendMessage(chatId, responseText);
        }
    }

    private String handleCommand(@NotNull String userId, @NotNull String command) {
        return switch (command.toLowerCase()) {
            case "/start" -> "Добро пожаловать! Используйте w,a,s,d для движения.";
            case "w" -> { gameEngine.moveForward(userId); yield gameEngine.describeEnvironment(userId); }
            case "a" -> { gameEngine.turnLeft(userId); yield gameEngine.describeEnvironment(userId); }
            case "d" -> { gameEngine.turnRight(userId); yield gameEngine.describeEnvironment(userId); }
            case "s" -> { gameEngine.turnBack(userId); yield gameEngine.describeEnvironment(userId); }
            default -> "Неизвестная команда: " + command;
        };
    }

    private void sendMessage(long chatId, String text) throws Exception {
        final String json = mapper.writeValueAsString(new Message(chatId, text));
        client.sendRequest("sendMessage", json);
    }

    private record Message(long chat_id, String text) {}
}