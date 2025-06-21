package com.temnenkov.mzctl.commands;

import com.temnenkov.mzctl.context.GameContext;
import com.temnenkov.mzctl.model.UserId;
import picocli.CommandLine;

@CommandLine.Command(name = "login", description = "Авторизация пользователя по логину")
public class Login implements Runnable {
    private final GameContext context;

    public Login(GameContext context) {
        this.context = context;
    }

    @CommandLine.Option(names = {"-u", "--user"}, description = "Логин пользователя", required = true)
    private String userId;

    @Override
    public void run() {
        context.setCurrentUserId(new UserId(userId));
        System.out.println("Вы авторизовались как: " + userId);
    }
}
