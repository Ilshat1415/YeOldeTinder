package ru.liga.telegrambot.service;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ButtonService {

    public InlineKeyboardMarkup getInlineMessageGender() {
        return createInlineKeyboardMarkup(
                createButton("Сударъ", "buttonMale"),
                createButton("Сударыня", "buttonFemale")
        );
    }

    public InlineKeyboardMarkup getInlineMessageSearchGender() {
        return createInlineKeyboardMarkup(
                createButton("Сударъ", "buttonMale"),
                createButton("Сударыня", "buttonFemale"),
                createButton("Всех", "buttonAll")
        );
    }

    public InlineKeyboardMarkup getInlineMessageMenu() {
        return createInlineKeyboardMarkup(
                createButton("Поиск", "buttonSearch"),
                createButton("Анкета", "buttonProfile"),
                createButton("Любимцы", "buttonFavorites")
        );
    }

    public InlineKeyboardMarkup getInlineMessageSearch() {
        return createInlineKeyboardMarkup(
                createButton("Влево", "buttonLeft"),
                createButton("Вправо", "buttonRight"),
                createButton("Меню", "buttonMenu")
        );
    }

    private InlineKeyboardButton createButton(String text, String callBackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callBackData)
                .build();
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(InlineKeyboardButton... buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            keyboardButtonsRow.add(buttons[i]);

            if (i > 0) {
                rowList.add(keyboardButtonsRow);
                keyboardButtonsRow = new ArrayList<>();
            }
        }

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
