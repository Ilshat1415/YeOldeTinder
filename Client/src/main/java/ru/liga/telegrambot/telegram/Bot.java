package ru.liga.telegrambot.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.handlers.UpdateHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Это telegram-бот.
 */
@Getter
@Setter
@Slf4j
public class Bot extends SpringWebhookBot {
    /**
     * Путь к боту.
     */
    private String botPath;
    /**
     * Имя telegram-бота.
     */
    private String botUsername;
    /**
     * Уникальный токен telegram-бота.
     */
    private String botToken;
    /**
     * Обработчик обновлений.
     */
    private UpdateHandler updateHandler;

    /**
     * Создание телеграмм-бота.
     *
     * @param setWebhook    объект SetWebhook
     * @param updateHandler объект обработчика обновлений
     */
    public Bot(SetWebhook setWebhook, UpdateHandler updateHandler) {
        super(setWebhook);
        this.updateHandler = updateHandler;

        log.info("Бот создан!");
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateHandler.handleUpdate(update);
    }

    /**
     * Отправка анкеты пользователю.
     *
     * @param chatId         id чата
     * @param user           объект User
     * @param keyboardMarkup разметка встраиваемой клавиатуры
     */
    public void sendPhoto(String chatId, User user, InlineKeyboardMarkup keyboardMarkup) {
        try {
            File file = File.createTempFile(chatId, ".jpg");
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(Base64.getDecoder().decode(user.getDescription()));
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(new InputFile(file))
                        .caption(user.getGender() + ", " + user.getName())
                        .replyMarkup(keyboardMarkup)
                        .build();
                this.execute(sendPhoto);
            }
            log.debug("Пользователь: {}. Анкета отправлена", user.getId());
        } catch (TelegramApiException e) {
            log.error("Ошибка {}. Отправка анкеты. Пользователь: {}",
                    e.getMessage(), user.getId(), e);
        } catch (IOException e) {
            log.error("Ошибка {}. Создание временного файла. Пользователь: {}",
                    e.getMessage(), user.getId(), e);
        }
    }
}
