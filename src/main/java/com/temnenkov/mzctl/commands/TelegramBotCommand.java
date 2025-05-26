package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.gameengine.GameEngine;
import com.temnenkov.mzctl.telegram.TelegramBotAdapter;
import com.temnenkov.mzctl.telegram.TelegramBotConfig;
import com.temnenkov.mzctl.telegram.TelegramHttpClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "telegram-bot", description = "Запускает Telegram-бота")
public class TelegramBotCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotCommand.class);

    @CommandLine.Option(names = "--config", required = true, description = "Путь к конфигурации")
    Path configPath;

    @NotNull
    private final GameEngine gameEngine;

    public TelegramBotCommand(@NotNull GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try {
            TelegramBotConfig config = new TelegramBotConfig(configPath);
            TelegramHttpClient client = new TelegramHttpClient(config);
            TelegramBotAdapter adapter = new TelegramBotAdapter(client, gameEngine, config);
            adapter.run();
        } catch (Exception e) {
            logger.error("Ошибка запуска Telegram-бота: {}", e.getMessage(), e);
        }
    }
}