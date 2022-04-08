package ru.liga.telegrambot.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Конфигурация telegram-бота.
 */
@Getter
@Component
public class BotConfig {
    /**
     * Путь к вебхуку.
     */
    @Value("${telegram.bot-webhookPath}")
    private String botPath;
    /**
     * Имя telegram-бота.
     */
    @Value("${telegram.bot-userName}")
    private String botUsername;
    /**
     * Уникальный токен telegram-бота.
     */
    @Value("${telegram.bot-token}")
    private String botToken;
}
