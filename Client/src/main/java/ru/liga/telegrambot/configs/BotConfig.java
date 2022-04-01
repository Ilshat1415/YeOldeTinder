package ru.liga.telegrambot.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BotConfig {
    @Value("${telegram.bot-webhookPath}")
    private String botPath;
    @Value("${telegram.bot-userName}")
    private String botUsername;
    @Value("${telegram.bot-token}")
    private String botToken;
}
