package ru.liga.telegrambot.service;


import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class ButtonService {
    private final InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

    public InlineKeyboardMarkup getInlineMessageGender() {
        InlineKeyboardButton buttonMale = new InlineKeyboardButton();
        buttonMale.setText("Сударъ");
        InlineKeyboardButton buttonFemale = new InlineKeyboardButton();
        buttonFemale.setText("Сударыня");

        buttonMale.setCallbackData("buttonMale");
        buttonFemale.setCallbackData("buttonFemale");

        setButtons(buttonMale, buttonFemale);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineMessageSearchGender() {
        InlineKeyboardButton buttonMale = new InlineKeyboardButton();
        buttonMale.setText("Сударъ");
        InlineKeyboardButton buttonFemale = new InlineKeyboardButton();
        buttonFemale.setText("Сударыня");
        InlineKeyboardButton buttonAll = new InlineKeyboardButton();
        buttonAll.setText("Всех");

        buttonMale.setCallbackData("buttonMale");
        buttonFemale.setCallbackData("buttonFemale");
        buttonAll.setCallbackData("buttonAll");

        setButtons(buttonMale, buttonFemale, buttonAll);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineMessageMenu() {
        InlineKeyboardButton buttonSearch = new InlineKeyboardButton();
        buttonSearch.setText("Поиск");
        InlineKeyboardButton buttonProfile = new InlineKeyboardButton();
        buttonProfile.setText("Анкета");
        InlineKeyboardButton buttonFavorites = new InlineKeyboardButton();
        buttonFavorites.setText("Любимцы");

        buttonSearch.setCallbackData("buttonSearch");
        buttonProfile.setCallbackData("buttonProfile");
        buttonFavorites.setCallbackData("buttonFavorites");

        setButtons(buttonSearch, buttonProfile, buttonFavorites);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineMessageSearch() {
        InlineKeyboardButton buttonLeft = new InlineKeyboardButton();
        buttonLeft.setText("Влево");
        InlineKeyboardButton buttonRight = new InlineKeyboardButton();
        buttonRight.setText("Вправо");
        InlineKeyboardButton buttonMenu = new InlineKeyboardButton();
        buttonMenu.setText("Меню");

        buttonLeft.setCallbackData("buttonLeft");
        buttonRight.setCallbackData("buttonRight");
        buttonMenu.setCallbackData("buttonMenu");

        setButtons(buttonLeft, buttonRight, buttonMenu);

        return inlineKeyboardMarkup;
    }

    private void setButtons(InlineKeyboardButton... buttons) {
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
    }
}
