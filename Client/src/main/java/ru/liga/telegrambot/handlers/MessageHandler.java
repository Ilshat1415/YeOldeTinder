package ru.liga.telegrambot.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.caches.UsersCache;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.KeyboardService;
import ru.liga.telegrambot.telegram.BotState;

/**
 * Обработчик сообщений.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandler {
    /**
     * Сервис клавиатур.
     */
    private final KeyboardService keyboardService;
    /**
     * Кеш состояний бота.
     */
    private final BotStateCache botStateCache;
    /**
     * Кеш пользователей.
     */
    private final UsersCache usersCache;

    /**
     * Обработка сообщений взависимости от текущего состояния telegram-бота.
     *
     * @param message  сообщение
     * @param botState состояние бота
     * @return ответ на сообщение
     */
    public BotApiMethod<?> handle(Message message, BotState botState) {
        SendMessage sendMessage = new SendMessage();

        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        sendMessage.setChatId(String.valueOf(chatId));

        switch (botState.name()) {
            case ("START"):
                usersCache.saveUserCache(userId, new User(userId));

                botStateCache.saveBotState(userId, BotState.ENTERGENDER);

                sendMessage.setText("Вы сударь иль сударыня?");
                sendMessage.setReplyMarkup(keyboardService.getGenderKeyboard());
                break;

            case ("ENTERGENDER"):
            case ("SETGENDER"):
                sendMessage.setText("Используйте меню для выбора.\n" +
                        "Вы сударь иль сударыня?");
                sendMessage.setReplyMarkup(keyboardService.getGenderKeyboard());
                break;

            case ("ENTERGENDERSEARCH"):
            case ("SETGENDERSEARCH"):
                sendMessage.setText("Используйте меню для выбора.\n" +
                        "Кого вы ищите?");
                sendMessage.setReplyMarkup(keyboardService.getGenderSearchKeyboard());
                break;

            case ("ENTERNAME"):
                usersCache.getUsers().get(userId).setName(message.getText());

                botStateCache.saveBotState(userId, BotState.ENTERDESCRIPTION);

                sendMessage.setText("Опишите себя.");
                break;

            case ("ENTERDESCRIPTION"):
                usersCache.getUsers().get(userId).setDescription(message.getText());

                botStateCache.saveBotState(userId, BotState.ENTERGENDERSEARCH);

                sendMessage.setText("Кого вы ищите?");
                sendMessage.setReplyMarkup(keyboardService.getGenderSearchKeyboard());
                break;

            case ("MENU"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
                break;

            case ("PROFILE"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(keyboardService.getProfileKeyboard());
                break;

            case ("CHANGE"):
                sendMessage.setText("Используйте представленное меню.\n" +
                        "Что желаете помѣнять?");
                sendMessage.setReplyMarkup(keyboardService.getChangeKeyboard());
                break;

            case ("SETNAME"):
                usersCache.getUsers().get(userId).setName(message.getText());

                botStateCache.saveBotState(userId, BotState.CHANGE);

                sendMessage.setText("Что ещё желаете помѣнять?");
                sendMessage.setReplyMarkup(keyboardService.getChangeKeyboard());
                break;

            case ("SETDESCRIPTION"):
                usersCache.getUsers().get(userId).setDescription(message.getText());

                botStateCache.saveBotState(userId, BotState.CHANGE);

                sendMessage.setText("Что ещё желаете помѣнять?");
                sendMessage.setReplyMarkup(keyboardService.getChangeKeyboard());
                break;

            case ("SEARCH"):
            case ("FAVORITES"):
                sendMessage.setText("Используйте представленное меню.");
                sendMessage.setReplyMarkup(keyboardService.getSearchKeyboard());
                break;

            default:
                log.debug("Пользователь: {}. Состояние бота: {}. Сообщение не обрабатывается.",
                        userId, botState.name());
                return null;
        }

        log.debug("Пользователь: {}. Состояние бота: {}. Сообщение обработано.",
                userId, botState.name());
        return sendMessage;
    }
}
