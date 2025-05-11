package com.temnenkov.mzctl.commands.util;

import com.temnenkov.mzctl.gameengine.GameEngine;
import picocli.CommandLine;

import java.lang.reflect.Constructor;

public class CommandFactory implements CommandLine.IFactory {
    private final GameEngine gameEngine;

    public CommandFactory(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        try {
            // Если у команды есть конструктор с GameEngine — используем его
            Constructor<K> constructor = cls.getDeclaredConstructor(GameEngine.class);
            return constructor.newInstance(gameEngine);
        } catch (NoSuchMethodException e) {
            // иначе используем конструктор без аргументов
            return cls.getDeclaredConstructor().newInstance();
        }
    }
}