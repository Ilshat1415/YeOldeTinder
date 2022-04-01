package ru.liga.telegrambot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.caches.UserCache;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.ButtonService;
import ru.liga.telegrambot.telegram.BotState;

@Component
@RequiredArgsConstructor
public class MessageHandler {
    private final BotStateCache botStateCache;
    private final ButtonService buttonService;
    private final UserCache userCache;

    public BotApiMethod<?> handle(Message message, BotState botState) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        switch (botState.name()) {
            case ("START"):
                botStateCache.saveBotState(userId, BotState.ENTERGENDER);
                userCache.saveUserCache(userId, new User());
                userCache.getUserMap().get(userId).setId(userId);
                sendMessage.setText("Вы сударъ иль сударыня?");
                sendMessage.setReplyMarkup(buttonService.getInlineMessageGender());
                return sendMessage;
            case ("ENTERGENDER"):
            case ("ENTERGENDERSEARCH"):
                sendMessage.setText("Выберете полъ.");
                sendMessage.setReplyMarkup(buttonService.getInlineKeyboardMarkup());
                return sendMessage;
            case ("ENTERNAME"):
                botStateCache.saveBotState(userId, BotState.ENTERDESCRIPTION);
                userCache.getUserMap().get(userId).setName(message.getText());
                sendMessage.setText("Опишите себя.");
                return sendMessage;
            case ("ENTERDESCRIPTION"):
                botStateCache.saveBotState(userId, BotState.ENTERGENDERSEARCH);
                userCache.getUserMap().get(userId).setDescription(message.getText());
                sendMessage.setText("Кого вы ищите?");
                sendMessage.setReplyMarkup(buttonService.getInlineMessageSearchGender());
                return sendMessage;
            case ("MENU"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
                return sendMessage;
            case ("SEARCH"):
            case ("FAVORITES"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(buttonService.getInlineKeyboardMarkup());
                return sendMessage;
            default:
                sendMessage.setText("Oops...");
                return sendMessage;
        }
    }
}
