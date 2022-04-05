package ru.liga.telegrambot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.caches.UsersCache;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.ButtonService;
import ru.liga.telegrambot.telegram.BotState;

@Component
@RequiredArgsConstructor
public class MessageHandler {
    private final BotStateCache botStateCache;
    private final ButtonService buttonService;
    private final UsersCache usersCache;

    public BotApiMethod<?> handle(Message message, BotState botState) {
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        switch (botState.name()) {
            case ("START"):
                botStateCache.saveBotState(userId, BotState.ENTERGENDER);
                usersCache.saveUserCache(userId, new User());
                usersCache.getUsers().get(userId).setId(userId);

                sendMessage.setText("Вы сударъ иль сударыня?");
                sendMessage.setReplyMarkup(buttonService.getGenderKeyboard());

                return sendMessage;
            case ("ENTERGENDER"):
            case ("SETGENDER"):
                sendMessage.setText("Используйте меню для выбора.\n" +
                        "Вы сударъ иль сударыня?");
                sendMessage.setReplyMarkup(buttonService.getGenderKeyboard());

                return sendMessage;
            case ("ENTERGENDERSEARCH"):
            case ("SETGENDERSEARCH"):
                sendMessage.setText("Используйте меню для выбора.\n" +
                        "Кого вы ищите?");
                sendMessage.setReplyMarkup(buttonService.getGenderSearchKeyboard());

                return sendMessage;
            case ("ENTERNAME"):
                botStateCache.saveBotState(userId, BotState.ENTERDESCRIPTION);
                usersCache.getUsers().get(userId).setName(message.getText());

                sendMessage.setText("Опишите себя.");

                return sendMessage;
            case ("ENTERDESCRIPTION"):
                botStateCache.saveBotState(userId, BotState.ENTERGENDERSEARCH);
                usersCache.getUsers().get(userId).setDescription(message.getText());

                sendMessage.setText("Кого вы ищите?");
                sendMessage.setReplyMarkup(buttonService.getGenderSearchKeyboard());

                return sendMessage;
            case ("MENU"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());

                return sendMessage;
            case ("CHANGE"):
                sendMessage.setText("Используйте представленное меню.\n" +
                        "Что желаете помѣнять?");
                sendMessage.setReplyMarkup(buttonService.getChangeKeyboard());

                return sendMessage;
            case ("SETNAME"):
                usersCache.getUsers().get(userId).setName(message.getText());

                botStateCache.saveBotState(userId, BotState.CHANGE);
                sendMessage.setText("Что ещё желаете помѣнять?");
                sendMessage.setReplyMarkup(buttonService.getChangeKeyboard());

                return sendMessage;
            case ("SETDESCRIPTION"):
                usersCache.getUsers().get(userId).setDescription(message.getText());

                botStateCache.saveBotState(userId, BotState.CHANGE);
                sendMessage.setText("Что ещё желаете помѣнять?");
                sendMessage.setReplyMarkup(buttonService.getChangeKeyboard());

                return sendMessage;
            case ("SEARCH"):
            case ("FAVORITES"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(buttonService.getSearchKeyboard());

                return sendMessage;
            default:
                return null;
        }
    }
}
