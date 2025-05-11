package com.temnenkov.mzctl.commands.util;

import com.temnenkov.mzctl.context.GameContext;
import picocli.CommandLine;

import java.lang.reflect.Constructor;

public class CommandFactory implements CommandLine.IFactory {
    private final GameContext context;

    public CommandFactory(GameContext context) {
        this.context = context;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        try {
            // Если у команды есть конструктор с GameContext — используем его
            Constructor<K> constructor = cls.getDeclaredConstructor(GameContext.class);
            return constructor.newInstance(context);
        } catch (NoSuchMethodException e) {
            // иначе используем конструктор без аргументов
            return cls.getDeclaredConstructor().newInstance();
        }
    }
}