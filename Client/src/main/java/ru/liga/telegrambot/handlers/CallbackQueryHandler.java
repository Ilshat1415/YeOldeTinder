package ru.liga.telegrambot.handlers;

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
import ru.liga.telegrambot.service.ButtonService;
import ru.liga.telegrambot.service.ServerDataService;
import ru.liga.telegrambot.telegram.Bot;
import ru.liga.telegrambot.telegram.BotState;

@Component
public class CallbackQueryHandler {
    private final BotStateCache botStateCache;
    private final ButtonService buttonService;
    private final ServerDataService serverDataService;
    private final UsersCache usersCache;
    private final SearchedUsersCache searchedUsersCache;
    private final FavoritesCache favoritesCache;
    private final Bot bot;

    public CallbackQueryHandler(BotStateCache botStateCache,
                                ButtonService buttonService,
                                ServerDataService serverDataService,
                                UsersCache usersCache,
                                SearchedUsersCache searchedUsersCache,
                                FavoritesCache favoritesCache,
                                @Lazy Bot bot) {
        this.botStateCache = botStateCache;
        this.buttonService = buttonService;
        this.serverDataService = serverDataService;
        this.usersCache = usersCache;
        this.searchedUsersCache = searchedUsersCache;
        this.favoritesCache = favoritesCache;
        this.bot = bot;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        BotState botState = botStateCache.getBotStateMap().get(userId) == null ?
                BotState.MENU : botStateCache.getBotStateMap().get(callbackQuery.getFrom().getId());

        switch (botState.name()) {
            case "ENTERGENDER":
                reactionForEnterGender(userId, sendMessage, data);
                return sendMessage;
            case "ENTERGENDERSEARCH":
                reactionForEnterGenderSearch(userId, sendMessage, data);
                return sendMessage;
            case "MENU":
                reactionForMenu(userId, sendMessage, data);
                return sendMessage;
            case "PROFILE":
                reactionForProfile(userId, sendMessage, data);
                return sendMessage;
            case "CHANGE":
                reactionForChange(userId, sendMessage, data);
                return sendMessage;
            case "SETGENDER":
                reactionForSetGender(userId, sendMessage, data);
                return sendMessage;
            case "SETGENDERSEARCH":
                reactionForSetGenderSearch(userId, sendMessage, data);
                return sendMessage;
            case "SEARCH":
                reactionForSearch(userId, sendMessage, data);
                return sendMessage;
            case "FAVORITES":
                reactionForFavorites(userId, sendMessage, data);
                return sendMessage;
            default:
                return null;
        }
    }

    private void reactionForEnterGender(long userId, SendMessage sendMessage, String data) {
        String gender = "buttonMale".equals(data) ? "Сударъ" : "Сударыня";
        usersCache.getUsers().get(userId).setGender(gender);

        botStateCache.saveBotState(userId, BotState.ENTERNAME);
        sendMessage.setText("Как вас величать?");
    }

    private void reactionForEnterGenderSearch(long userId, SendMessage sendMessage, String data) {
        String genderSearch;
        if ("buttonMale".equals(data)) {
            genderSearch = "Сударъ";
        } else if ("buttonFemale".equals(data)) {
            genderSearch = "Сударыня";
        } else {
            genderSearch = "Всех";
        }
        usersCache.getUsers().get(userId).setGenderSearch(genderSearch);

        User user = serverDataService.createProfile(usersCache.getUsers().get(userId));

        usersCache.dump(userId);
        botStateCache.saveBotState(userId, BotState.MENU);
        bot.sendPhoto(sendMessage.getChatId(), user, buttonService.getMenuKeyboard());
    }

    private void reactionForMenu(long userId, SendMessage sendMessage, String data) {
        if ("buttonProfile".equals(data)) {
            botStateCache.saveBotState(userId, BotState.PROFILE);
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    serverDataService.getUserImageById(userId),
                    buttonService.getProfileKeyboard()
            );
        } else if ("buttonSearch".equals(data)) {
            searchedUsersCache.refresh(userId);
            User foundUser = searchedUsersCache.getSearchedUsersById(userId);

            if (foundUser != null) {
                botStateCache.saveBotState(userId, BotState.SEARCH);
                bot.sendPhoto(sendMessage.getChatId(), foundUser, buttonService.getSearchKeyboard());
            } else {
                sendMessage.setText("Ни кого нѣтъ.");
                sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());
            }
        } else if ("buttonFavorites".equals(data)) {
            User favorite = favoritesCache.getProfileForUserFavorites(userId);

            if (favorite != null) {
                botStateCache.saveBotState(userId, BotState.FAVORITES);
                bot.sendPhoto(sendMessage.getChatId(), favorite, buttonService.getSearchKeyboard());
            } else {
                sendMessage.setText("У васъ нѣтъ любимцевъ.");
                sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());
            }
        }
    }

    private void reactionForProfile(long userId, SendMessage sendMessage, String data) {
        if ("buttonChange".equals(data)) {
            botStateCache.saveBotState(userId, BotState.CHANGE);
            usersCache.saveUserCache(userId, serverDataService.getUserById(userId));
            sendMessage.setText("Что желаете помѣнять?");
            sendMessage.setReplyMarkup(buttonService.getChangeKeyboard());
        } else if ("buttonMenu".equals(data)) {
            botStateCache.saveBotState(userId, BotState.MENU);
            sendMessage.setText("Возвратъ въ меню");
            sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());
        }
    }

    private void reactionForChange(long userId, SendMessage sendMessage, String data) {
        if ("buttonGender".equals(data)) {
            botStateCache.saveBotState(userId, BotState.SETGENDER);
            sendMessage.setText("Вы сударъ иль сударыня?");
            sendMessage.setReplyMarkup(buttonService.getGenderKeyboard());
        } else if ("buttonName".equals(data)) {
            botStateCache.saveBotState(userId, BotState.SETNAME);
            sendMessage.setText("Как вас величать?");
        } else if ("buttonDescription".equals(data)) {
            botStateCache.saveBotState(userId, BotState.SETDESCRIPTION);
            sendMessage.setText("Опишите себя.");
        } else if ("buttonGenderSearch".equals(data)) {
            botStateCache.saveBotState(userId, BotState.SETGENDERSEARCH);
            sendMessage.setText("Кого вы ищите?");
            sendMessage.setReplyMarkup(buttonService.getGenderSearchKeyboard());
        } else if ("buttonSave".equals(data)) {
            botStateCache.saveBotState(userId, BotState.PROFILE);
            sendMessage.setText("Сохраненія измѣнены.");

            User user = serverDataService.createProfile(usersCache.getUsers().get(userId));
            usersCache.dump(userId);

            bot.sendPhoto(sendMessage.getChatId(), user, buttonService.getProfileKeyboard());
        } else if ("buttonCancel".equals(data)) {
            botStateCache.saveBotState(userId, BotState.PROFILE);
            sendMessage.setText("Измѣненія отмѣнены.");
            usersCache.dump(userId);

            bot.sendPhoto(
                    sendMessage.getChatId(),
                    serverDataService.getUserImageById(userId),
                    buttonService.getProfileKeyboard()
            );
        }
    }

    private void reactionForSetGender(long userId, SendMessage sendMessage, String data) {
        String gender = "buttonMale".equals(data) ? "Сударъ" : "Сударыня";
        usersCache.getUsers().get(userId).setGender(gender);

        botStateCache.saveBotState(userId, BotState.CHANGE);
        sendMessage.setText("Что ещё желаете помѣнять?");
        sendMessage.setReplyMarkup(buttonService.getChangeKeyboard());
    }

    private void reactionForSetGenderSearch(long userId, SendMessage sendMessage, String data) {
        String genderSearch;
        if ("buttonMale".equals(data)) {
            genderSearch = "Сударъ";
        } else if ("buttonFemale".equals(data)) {
            genderSearch = "Сударыня";
        } else {
            genderSearch = "Всех";
        }
        usersCache.getUsers().get(userId).setGenderSearch(genderSearch);

        botStateCache.saveBotState(userId, BotState.CHANGE);
        sendMessage.setText("Что ещё желаете помѣнять?");
        sendMessage.setReplyMarkup(buttonService.getChangeKeyboard());
    }

    private void reactionForSearch(long userId, SendMessage sendMessage, String data) {
        if ("buttonLeft".equals(data)) {
            bot.sendPhoto(
                    sendMessage.getChatId(),
                    searchedUsersCache.getSearchedUsersById(userId),
                    buttonService.getSearchKeyboard()
            );
        } else if ("buttonRight".equals(data)) {
            String answer = serverDataService.likeUserById(userId, searchedUsersCache.getFoundUserById(userId));

            if (answer != null) {
                sendMessage.setText(answer);
            }
            User foundUser = searchedUsersCache.getSearchedUsersById(userId);

            if (foundUser != null) {
                bot.sendPhoto(sendMessage.getChatId(), foundUser, buttonService.getSearchKeyboard());
            } else {
                searchedUsersCache.dump(userId);
                botStateCache.saveBotState(userId, BotState.MENU);
                sendMessage.setText("Больше нѣтъ анкетъ, возвратъ въ меню");
                sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());
            }
        } else if ("buttonMenu".equals(data)) {
            searchedUsersCache.dump(userId);
            botStateCache.saveBotState(userId, BotState.MENU);
            sendMessage.setText("Возвратъ въ меню");
            sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());
        }
    }

    private void reactionForFavorites(long userId, SendMessage sendMessage, String data) {
        if ("buttonLeft".equals(data)) {
            User favorite = favoritesCache.userFavoritesLeft(userId);
            bot.sendPhoto(sendMessage.getChatId(), favorite, buttonService.getSearchKeyboard());
        } else if ("buttonRight".equals(data)) {
            User favorite = favoritesCache.userFavoritesRight(userId);
            bot.sendPhoto(sendMessage.getChatId(), favorite, buttonService.getSearchKeyboard());
        } else if ("buttonMenu".equals(data)) {
            favoritesCache.dump(userId);
            botStateCache.saveBotState(userId, BotState.MENU);
            sendMessage.setText("Возвратъ въ меню");
            sendMessage.setReplyMarkup(buttonService.getMenuKeyboard());
        }
    }
}
