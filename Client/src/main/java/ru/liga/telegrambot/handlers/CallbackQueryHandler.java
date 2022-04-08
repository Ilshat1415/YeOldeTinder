package ru.liga.telegrambot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.caches.FavoritesCache;
import ru.liga.telegrambot.caches.SearchedUsersCache;
import ru.liga.telegrambot.caches.UsersCache;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.KeyboardService;
import ru.liga.telegrambot.service.ServerDataService;
import ru.liga.telegrambot.telegram.Bot;
import ru.liga.telegrambot.telegram.BotState;

/**
 * Обработчик нажатий на встроенные в сообщения кнопки.
 */
@Slf4j
@Component
public class CallbackQueryHandler {
    /**
     * Сервис серверных данных.
     */
    private final ServerDataService serverDataService;
    /**
     * Сервис клавиатур.
     */
    private final KeyboardService keyboardService;
    /**
     * Кеш искомых пользователей.
     */
    private final SearchedUsersCache searchedUsersCache;
    /**
     * Кеш любимцев.
     */
    private final FavoritesCache favoritesCache;
    /**
     * Кеш состояний бота.
     */
    private final BotStateCache botStateCache;
    /**
     * Кеш пользователей.
     */
    private final UsersCache usersCache;
    /**
     * Telegram-бот.
     */
    private final Bot bot;

    public CallbackQueryHandler(
            ServerDataService serverDataService,
            KeyboardService keyboardService,
            SearchedUsersCache searchedUsersCache,
            FavoritesCache favoritesCache,
            BotStateCache botStateCache,
            UsersCache usersCache,
            @Lazy Bot bot
    ) {
        this.serverDataService = serverDataService;
        this.keyboardService = keyboardService;
        this.searchedUsersCache = searchedUsersCache;
        this.favoritesCache = favoritesCache;
        this.botStateCache = botStateCache;
        this.usersCache = usersCache;
        this.bot = bot;
    }

    /**
     * Обработка обратного запроса от пользователя, такого как нажатие на кнопки.
     *
     * @param callbackQuery обратный запрос
     * @return ответ на запрос
     */
    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        SendMessage sendMessage = new SendMessage();

        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        String buttonData = callbackQuery.getData();

        sendMessage.setChatId(String.valueOf(chatId));

        BotState botState = botStateCache.getBotStateMap().get(userId) == null ?
                BotState.MENU : botStateCache.getBotStateMap().get(callbackQuery.getFrom().getId());

        switch (botState.name()) {
            case "ENTERGENDER":
                reactionForEnterGender(userId, sendMessage, buttonData);
                break;

            case "ENTERGENDERSEARCH":
                reactionForEnterGenderSearch(userId, sendMessage, buttonData);
                break;

            case "MENU":
                reactionForMenu(userId, sendMessage, buttonData);
                break;

            case "PROFILE":
                reactionForProfile(userId, sendMessage, buttonData);
                break;

            case "CHANGE":
                reactionForChange(userId, sendMessage, buttonData);
                break;

            case "SETGENDER":
                reactionForSetGender(userId, sendMessage, buttonData);
                break;

            case "SETGENDERSEARCH":
                reactionForSetGenderSearch(userId, sendMessage, buttonData);
                break;

            case "SEARCH":
                reactionForSearch(userId, sendMessage, buttonData);
                break;

            case "FAVORITES":
                reactionForFavorites(userId, sendMessage, buttonData);
                break;

            default:
                log.debug("Пользователь: {}. Состояние бота: {}. Кнопка: {}. Запрос не обрабатывается.",
                        userId, botState.name(), buttonData);
                return null;
        }

        log.debug("Пользователь: {}. Состояние бота: {}. Кнопка: {}. Запрос обработан.",
                userId, botState.name(), buttonData);
        return sendMessage;
    }

    /**
     * Рекация на выбор пола.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForEnterGender(long userId, SendMessage sendMessage, String buttonData) {
        String gender = "buttonMale".equals(buttonData) ? "Сударь" : "Сударыня";
        usersCache.getUsers().get(userId).setGender(gender);

        botStateCache.saveBotState(userId, BotState.ENTERNAME);

        sendMessage.setText("Как вас величать?");
    }

    /**
     * Рекация на выбор искомого пола.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForEnterGenderSearch(long userId, SendMessage sendMessage, String buttonData) {
        String genderSearch;
        if ("buttonMale".equals(buttonData)) {
            genderSearch = "Сударь";

        } else if ("buttonFemale".equals(buttonData)) {
            genderSearch = "Сударыня";

        } else {
            genderSearch = "Всех";
        }

        usersCache.getUsers().get(userId).setGenderSearch(genderSearch);
        User user = serverDataService.createProfile(usersCache.getUsers().get(userId));
        usersCache.dump(userId);

        botStateCache.saveBotState(userId, BotState.MENU);

        bot.sendPhoto(
                sendMessage.getChatId(),
                user,
                keyboardService.getMenuKeyboard()
        );
    }

    /**
     * Рекация на использование клавиатуры в меню.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForMenu(long userId, SendMessage sendMessage, String buttonData) {
        if ("buttonProfile".equals(buttonData)) {
            botStateCache.saveBotState(userId, BotState.PROFILE);

            bot.sendPhoto(
                    sendMessage.getChatId(),
                    serverDataService.getUserImageById(userId),
                    keyboardService.getProfileKeyboard()
            );
        } else if ("buttonSearch".equals(buttonData)) {
            searchedUsersCache.refresh(userId);
            User foundUser = searchedUsersCache.getSearchedUsersById(userId);
            if (foundUser != null) {
                botStateCache.saveBotState(userId, BotState.SEARCH);

                bot.sendPhoto(
                        sendMessage.getChatId(),
                        foundUser,
                        keyboardService.getSearchKeyboard()
                );

            } else {
                sendMessage.setText("Ни кого нѣтъ.");
                sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
            }

        } else if ("buttonFavorites".equals(buttonData)) {
            User favorite = favoritesCache.getProfileForUserFavorites(userId);
            if (favorite != null) {
                botStateCache.saveBotState(userId, BotState.FAVORITES);

                bot.sendPhoto(
                        sendMessage.getChatId(),
                        favorite,
                        keyboardService.getSearchKeyboard()
                );

            } else {
                sendMessage.setText("У васъ нѣтъ любимцевъ.");
                sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
            }
        }
    }

    /**
     * Рекация на использование клавиатуры в профиле.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForProfile(long userId, SendMessage sendMessage, String buttonData) {
        if ("buttonChange".equals(buttonData)) {
            usersCache.saveUserCache(userId, serverDataService.getUserById(userId));

            botStateCache.saveBotState(userId, BotState.CHANGE);

            sendMessage.setText("Что желаете помѣнять?");
            sendMessage.setReplyMarkup(keyboardService.getChangeKeyboard());

        } else if ("buttonMenu".equals(buttonData)) {
            botStateCache.saveBotState(userId, BotState.MENU);

            sendMessage.setText("Возвратъ въ меню");
            sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
        }
    }

    /**
     * Рекация на использование клавиатуры в изменении профиля.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForChange(long userId, SendMessage sendMessage, String buttonData) {
        if ("buttonGender".equals(buttonData)) {
            botStateCache.saveBotState(userId, BotState.SETGENDER);

            sendMessage.setText("Вы сударь иль сударыня?");
            sendMessage.setReplyMarkup(keyboardService.getGenderKeyboard());

        } else if ("buttonName".equals(buttonData)) {
            botStateCache.saveBotState(userId, BotState.SETNAME);

            sendMessage.setText("Как вас величать?");

        } else if ("buttonDescription".equals(buttonData)) {
            botStateCache.saveBotState(userId, BotState.SETDESCRIPTION);

            sendMessage.setText("Опишите себя.");

        } else if ("buttonGenderSearch".equals(buttonData)) {
            botStateCache.saveBotState(userId, BotState.SETGENDERSEARCH);

            sendMessage.setText("Кого вы ищите?");
            sendMessage.setReplyMarkup(keyboardService.getGenderSearchKeyboard());

        } else if ("buttonSave".equals(buttonData)) {
            User user = serverDataService.createProfile(usersCache.getUsers().get(userId));
            usersCache.dump(userId);

            botStateCache.saveBotState(userId, BotState.PROFILE);

            sendMessage.setText("Сохраненія измѣнены.");
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    user,
                    keyboardService.getProfileKeyboard()
            );

        } else if ("buttonCancel".equals(buttonData)) {
            usersCache.dump(userId);

            botStateCache.saveBotState(userId, BotState.PROFILE);

            sendMessage.setText("Измѣненія отмѣнены.");
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    serverDataService.getUserImageById(userId),
                    keyboardService.getProfileKeyboard()
            );
        }
    }

    /**
     * Рекация на изменение пола.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForSetGender(long userId, SendMessage sendMessage, String buttonData) {
        String gender = "buttonMale".equals(buttonData) ? "Сударь" : "Сударыня";
        usersCache.getUsers().get(userId).setGender(gender);

        botStateCache.saveBotState(userId, BotState.CHANGE);

        sendMessage.setText("Что ещё желаете помѣнять?");
        sendMessage.setReplyMarkup(keyboardService.getChangeKeyboard());
    }

    /**
     * Рекация на изменение искомого пола.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForSetGenderSearch(long userId, SendMessage sendMessage, String buttonData) {
        String genderSearch;
        if ("buttonMale".equals(buttonData)) {
            genderSearch = "Сударь";

        } else if ("buttonFemale".equals(buttonData)) {
            genderSearch = "Сударыня";

        } else {
            genderSearch = "Всех";
        }

        usersCache.getUsers().get(userId).setGenderSearch(genderSearch);

        botStateCache.saveBotState(userId, BotState.CHANGE);

        sendMessage.setText("Что ещё желаете помѣнять?");
        sendMessage.setReplyMarkup(keyboardService.getChangeKeyboard());
    }

    /**
     * Рекация на использование клавиатуры в поиске.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForSearch(long userId, SendMessage sendMessage, String buttonData) {
        if ("buttonLeft".equals(buttonData)) {
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    searchedUsersCache.getSearchedUsersById(userId),
                    keyboardService.getSearchKeyboard()
            );

        } else if ("buttonRight".equals(buttonData)) {
            String answer = serverDataService.likeUserById(userId, searchedUsersCache.getFoundUserById(userId));
            if (answer != null) {
                sendMessage.setText(answer);
            }

            User foundUser = searchedUsersCache.getSearchedUsersById(userId);
            if (foundUser != null) {
                bot.sendPhoto(
                        sendMessage.getChatId(),
                        foundUser,
                        keyboardService.getSearchKeyboard()
                );

            } else {
                searchedUsersCache.dump(userId);

                botStateCache.saveBotState(userId, BotState.MENU);

                sendMessage.setText("Больше нѣтъ анкетъ, возвратъ въ меню");
                sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
            }

        } else if ("buttonMenu".equals(buttonData)) {
            searchedUsersCache.dump(userId);

            botStateCache.saveBotState(userId, BotState.MENU);

            sendMessage.setText("Возвратъ въ меню");
            sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
        }
    }

    /**
     * Рекация на использование клавиатуры в любимцах.
     *
     * @param userId      id пользователя
     * @param sendMessage заготовка сообщения
     * @param buttonData  данные о кнопке
     */
    private void reactionForFavorites(long userId, SendMessage sendMessage, String buttonData) {
        if ("buttonLeft".equals(buttonData)) {
            User favorite = favoritesCache.userFavoritesLeft(userId);
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    favorite,
                    keyboardService.getSearchKeyboard()
            );

        } else if ("buttonRight".equals(buttonData)) {
            User favorite = favoritesCache.userFavoritesRight(userId);
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    favorite,
                    keyboardService.getSearchKeyboard()
            );

        } else if ("buttonMenu".equals(buttonData)) {
            favoritesCache.dump(userId);

            botStateCache.saveBotState(userId, BotState.MENU);

            sendMessage.setText("Возвратъ въ меню");
            sendMessage.setReplyMarkup(keyboardService.getMenuKeyboard());
        }
    }
}
