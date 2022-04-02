package ru.liga.telegrambot.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.handlers.CallbackQueryHandler;
import ru.liga.telegrambot.handlers.MessageHandler;
import ru.liga.telegrambot.service.ServerDataService;

@Component
@RequiredArgsConstructor
public class TelegramFacade {
    private final CallbackQueryHandler callbackQueryHandler;
    private final ServerDataService serverDataService;
    private final MessageHandler messageHandler;
    private final BotStateCache botStateCache;

    public BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();

            if (message != null && message.hasText()) {
                return handleInputMessage(message);
            }
        }
        return null;
    }

    private BotApiMethod<?> handleInputMessage(Message message) {
        Long userId = message.getFrom().getId();
        BotState botState;

        if (botStateCache.getBotStateMap().get(userId) == null) {
            botState = serverDataService.getUserById(userId) == null ?
                    BotState.START : BotState.MENU;
            botStateCache.saveBotState(userId, botState);
        } else {
            botState = botStateCache.getBotStateMap().get(userId);
        }

        return messageHandler.handle(message, botState);
    }
}
