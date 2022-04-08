package ru.liga.telegrambot.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.telegrambot.telegram.Bot;

/**
 * Webhook контроллер для ловли обновлений.
 */
@RestController
@RequiredArgsConstructor
public class WebhookController {
    /**
     * Telegram-бот.
     */
    private final Bot bot;

    /**
     * Реакция на POST запрос.
     *
     * @param update поступающее обновление
     * @return ответ на обновление
     */
    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }
}
