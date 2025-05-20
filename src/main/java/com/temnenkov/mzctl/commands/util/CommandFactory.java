package com.temnenkov.mzctl.commands.util;

import com.temnenkov.mzctl.di.SimpleDIContainer;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

public class CommandFactory implements CommandLine.IFactory {
    @NotNull
    private final SimpleDIContainer container;

    public CommandFactory(@NotNull SimpleDIContainer container) {
        this.container = container;
    }

    @Override
    public <K> K create(Class<K> cls) {
        return container.createBean(cls);
    }
}