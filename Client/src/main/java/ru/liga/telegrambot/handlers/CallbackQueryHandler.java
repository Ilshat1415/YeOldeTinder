package ru.liga.telegrambot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.caches.FavoritesCache;
import ru.liga.telegrambot.caches.SearchedUsersCache;
import ru.liga.telegrambot.caches.UserCache;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.ButtonService;
import ru.liga.telegrambot.service.ServerDataService;
import ru.liga.telegrambot.telegram.BotState;

@Component
@RequiredArgsConstructor
public class CallbackQueryHandler {
    private final BotStateCache botStateCache;
    private final ButtonService buttonService;
    private final ServerDataService serverDataService;
    private final UserCache userCache;
    private final SearchedUsersCache searchedUsersCache;
    private final FavoritesCache favoritesCache;

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
        userCache.getUserMap().get(userId).setGender(gender);

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
        userCache.getUserMap().get(userId).setGenderSearch(genderSearch);

        User user = userCache.getUserMap().get(userId);
        String answer = serverDataService.createProfile(user);

        botStateCache.saveBotState(userId, BotState.MENU);
        sendMessage.setText(answer + "\n\n" + user);
        sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
    }

    private void reactionForMenu(long userId, SendMessage sendMessage, String data) {
        if ("buttonProfile".equals(data)) {
            User userProfile = serverDataService.getUserById(userId);
            sendMessage.setText(userProfile.toString());
            sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
        } else if ("buttonSearch".equals(data)) {
            searchedUsersCache.refreshCache(userId);
            User foundUser = searchedUsersCache.getSearchedUsersById(userId);
            if (foundUser != null) {
                botStateCache.saveBotState(userId, BotState.SEARCH);
                sendMessage.setText(foundUser.toString());
                sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
            } else {
                sendMessage.setText("Ни кого нѣтъ.");
            }
        } else if ("buttonFavorites".equals(data)) {
            User favorite = favoritesCache.getProfileForUserFavorites(userId);
            if (favorite != null) {
                botStateCache.saveBotState(userId, BotState.FAVORITES);
                sendMessage.setText(favorite.toString());
                sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
            } else {
                sendMessage.setText("У васъ нѣтъ любимцевъ.");
            }
        }
    }

    private void reactionForSearch(long userId, SendMessage sendMessage, String data) {
        if ("buttonLeft".equals(data)) {
            sendMessage.setText(searchedUsersCache.getSearchedUsersById(userId).toString());
            sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
        } else if ("buttonRight".equals(data)) {
            String answerFavorite = serverDataService.likeUserById(userId, searchedUsersCache.getFoundUserId(userId));

            sendMessage.setText(answerFavorite + "\n\n" +
                    searchedUsersCache.getSearchedUsersById(userId));
            sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
        } else if ("buttonMenu".equals(data)) {
            botStateCache.saveBotState(userId, BotState.MENU);
            sendMessage.setText("Возврат в меню");
            sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
        }
    }

    private void reactionForFavorites(long userId, SendMessage sendMessage, String data) {
        if ("buttonLeft".equals(data)) {
            sendMessage.setText(favoritesCache.userFavoritesLeft(userId).toString());
            sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
        } else if ("buttonRight".equals(data)) {
            sendMessage.setText(favoritesCache.userFavoritesRight(userId).toString());
            sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
        } else if ("buttonMenu".equals(data)) {
            botStateCache.saveBotState(userId, BotState.MENU);
            sendMessage.setText("Возврат в меню");
            sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
        }
    }
}
