package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.di.SimpleDIContainer;
import com.temnenkov.mzctl.telegram.TelegramBotAdapter;
import com.temnenkov.mzctl.telegram.TelegramBotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;

public class TelegramBotCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotCommand.class);

    @CommandLine.Option(names = "--config", required = true, description = "Путь к конфигурации")
    private Path configPath;

    private final SimpleDIContainer container;

    public TelegramBotCommand(SimpleDIContainer container) {
        this.container = container;
    }

    @Override
    public void run() {
        try {
            TelegramBotConfig config = new TelegramBotConfig(configPath);
            container.registerBean(TelegramBotConfig.class, config);

            TelegramBotAdapter adapter = container.createBean(TelegramBotAdapter.class);
            adapter.run();
        } catch (Exception e) {
            logger.error("Ошибка запуска Telegram-бота: {}", e.getMessage(), e);
        }
    }
}