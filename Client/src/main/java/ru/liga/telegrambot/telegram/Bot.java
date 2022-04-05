package ru.liga.telegrambot.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.liga.telegrambot.entities.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@Getter
@Setter
public class Bot extends SpringWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;

    private TelegramFacade telegramFacade;

    public Bot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        super(setWebhook);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramFacade.handleUpdate(update);
    }

    @SneakyThrows
    public void sendPhoto(String chatId, User user, InlineKeyboardMarkup keyboardMarkup) {
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
        } finally {
            file.delete();
        }
    }
}
