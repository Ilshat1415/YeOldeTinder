package ru.liga.telegrambot.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.liga.telegrambot.telegram.Bot;
import ru.liga.telegrambot.handlers.UpdateHandler;

/**
 * Конфигурация приложения.
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {
    /**
     * Конфигурация telegram-бота.
     */
    private final BotConfig botConfig;

    /**
     * Создание бина SetWebhook.
     *
     * @return SetWebhook
     */
    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder()
                .url(botConfig.getBotPath())
                .build();
    }

    /**
     * Создание бина telegram-бота и его настройка.
     *
     * @param setWebhook объект SetWebhook
     * @param updateHandler объект обработчика обновлений.
     * @return объект telegram-бота
     */
    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, UpdateHandler updateHandler) {
        Bot bot = new Bot(setWebhook, updateHandler);
        bot.setBotPath(botConfig.getBotPath());
        bot.setBotUsername(botConfig.getBotUsername());
        bot.setBotToken(botConfig.getBotToken());

        return bot;
    }

    /**
     * Создание бина RestTemplate.
     *
     * @return RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
