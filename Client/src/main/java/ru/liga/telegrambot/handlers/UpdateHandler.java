package ru.liga.telegrambot.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.service.ServerDataService;
import ru.liga.telegrambot.telegram.BotState;

/**
 * Обработчик, который обрабатывает переданное обновление.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateHandler {
    /**
     * Обработчик нажатий кнопок на интерфейсе.
     */
    private final CallbackQueryHandler callbackQueryHandler;
    /**
     * Сервис связи с сервером.
     */
    private final ServerDataService serverDataService;
    /**
     * Обработчик сообщений.
     */
    private final MessageHandler messageHandler;
    /**
     * Кеш с состояниями бота.
     */
    private final BotStateCache botStateCache;

    /**
     * Обработка обновлений, где определяется какой тип обновления поступил.
     *
     * @param update обновление
     * @return ответ на обновление
     */
    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());

        } else {
            return handleInputMessage(update.getMessage());
        }
    }

    /**
     * Обрабатывает входящее сообщение.
     *
     * @param message сообщение
     * @return ответ на сообщение
     */
    private BotApiMethod<?> handleInputMessage(Message message) {
        Long userId = message.getFrom().getId();
        if (!message.hasText()) {
            log.debug("Пользователь: {}. Было получено сообщение без текста.",
                    userId);

            return SendMessage.builder()
                    .chatId(userId.toString())
                    .text("Я васъ не понимаю.")
                    .build();
        }

        BotState botState;
        if (botStateCache.getBotStateMap().get(userId) == null) {
            botState = serverDataService.existsUserById(userId) ?
                    BotState.MENU : BotState.START;
            botStateCache.saveBotState(userId, botState);

        } else {
            botState = botStateCache.getBotStateMap().get(userId);
        }

        return messageHandler.handle(message, botState);
    }
}
