package ru.liga.telegrambot.service;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ButtonService {

    public InlineKeyboardMarkup getGenderKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Сударъ", "buttonMale"),
                createButton("Сударыня", "buttonFemale")
        );
    }

    public InlineKeyboardMarkup getGenderSearchKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Сударъ", "buttonMale"),
                createButton("Сударыня", "buttonFemale"),
                createButton("Всех", "buttonAll")
        );
    }

    public InlineKeyboardMarkup getMenuKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Поиск", "buttonSearch"),
                createButton("Анкета", "buttonProfile"),
                createButton("Любимцы", "buttonFavorites")
        );
    }

    public InlineKeyboardMarkup getProfileKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Изменить", "buttonChange"),
                createButton("Меню", "buttonMenu")
        );
    }

    public InlineKeyboardMarkup getChangeKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Пол", "buttonGender"),
                createButton("Имя", "buttonName"),
                createButton("Пол искомых анкет", "buttonGenderSearch"),
                createButton("Описание", "buttonDescription"),
                createButton("Сохранить", "buttonSave"),
                createButton("Отменить", "buttonCancel")
        );
    }

    public InlineKeyboardMarkup getSearchKeyboard() {
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
