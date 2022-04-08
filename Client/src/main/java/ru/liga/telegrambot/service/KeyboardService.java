package ru.liga.telegrambot.service;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис создания клавиатур для встраивания в сообщения.
 */
@Service
public class KeyboardService {

    /**
     * Получение клавиатуры для выбора пола.
     *
     * @return клавиатура для выбора пола
     */
    public InlineKeyboardMarkup getGenderKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Сударь", "buttonMale"),
                createButton("Сударыня", "buttonFemale")
        );
    }

    /**
     * Получение клавиатуры для выбора искомого пола.
     *
     * @return клавиатура для выбора искомого пола
     */
    public InlineKeyboardMarkup getGenderSearchKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Сударь", "buttonMale"),
                createButton("Сударыня", "buttonFemale"),
                createButton("Всех", "buttonAll")
        );
    }

    /**
     * Получение клавиатуры меню.
     *
     * @return клавиатура меню
     */
    public InlineKeyboardMarkup getMenuKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Поиск", "buttonSearch"),
                createButton("Анкета", "buttonProfile"),
                createButton("Любимцы", "buttonFavorites")
        );
    }

    /**
     * Получение клавиатуры профиль.
     *
     * @return клавиатура профиль
     */
    public InlineKeyboardMarkup getProfileKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Изменить", "buttonChange"),
                createButton("Меню", "buttonMenu")
        );
    }

    /**
     * Получение клавиатуры для изменния профиля.
     *
     * @return клавиатура для изменния профиля
     */
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

    /**
     * Получение клавиатуры для перебор пользователей.
     *
     * @return клавиатура для перебор пользователей
     */
    public InlineKeyboardMarkup getSearchKeyboard() {
        return createInlineKeyboardMarkup(
                createButton("Влево", "buttonLeft"),
                createButton("Вправо", "buttonRight"),
                createButton("Меню", "buttonMenu")
        );
    }

    /**
     * Создание встраиваемой кнопки.
     *
     * @param text         текст на кнопке
     * @param callBackData название кнопки
     * @return кнопка
     */
    private InlineKeyboardButton createButton(String text, String callBackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callBackData)
                .build();
    }

    /**
     * Создание из переданных кнопок встроенную разметку клавиатуры.
     *
     * @param buttons кнопки
     * @return клавиатура для встраивания в сообщение
     */
    private InlineKeyboardMarkup createInlineKeyboardMarkup(InlineKeyboardButton... buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

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
