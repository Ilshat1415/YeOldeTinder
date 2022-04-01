package ru.liga.telegrambot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.liga.telegrambot.caches.BotStateCache;
import ru.liga.telegrambot.caches.UserCache;
import ru.liga.telegrambot.caches.UsersCache;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.ButtonService;
import ru.liga.telegrambot.telegram.BotState;

@Component
@RequiredArgsConstructor
public class CallbackQueryHandler {
    private final BotStateCache botStateCache;
    private final ButtonService buttonService;
    private final UserCache userCache;
    private final UsersCache users;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        long userId = callbackQuery.getFrom().getId();
        long chatId = callbackQuery.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        RestTemplate restTemplate = new RestTemplate();

        BotState botState = botStateCache.getBotStateMap().get(userId) == null ?
                BotState.MENU : botStateCache.getBotStateMap().get(callbackQuery.getFrom().getId());

        String data = callbackQuery.getData();
        switch (botState.name()) {
            case "ENTERGENDER":
                String gender = "buttonMale".equals(data) ? "Сударъ" : "Сударыня";
                userCache.getUserMap().get(userId).setGender(gender);
                botStateCache.saveBotState(userId, BotState.ENTERNAME);
                sendMessage.setText("Как вас величать?");

                return sendMessage;

            case "ENTERGENDERSEARCH":
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
                String answer = restTemplate.postForObject(
                        "http://localhost:8080/createProfile",
                        user,
                        String.class
                );

                botStateCache.saveBotState(userId, BotState.MENU);

                sendMessage.setText(answer + "\n\n" + user);
                sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());

                return sendMessage;

            case "MENU":
                if ("buttonProfile".equals(data)) {
                    User userProfile = restTemplate.getForObject(
                            "http://localhost:8080/users/" + userId,
                            User.class
                    );
                    sendMessage.setText(userProfile.toString());
                    sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
                } else if ("buttonSearch".equals(data)) {
                    User foundUser = users.getProfileForUser(userId);
                    if (foundUser != null) {
                        botStateCache.saveBotState(userId, BotState.SEARCH);
                        sendMessage.setText(foundUser.toString());
                        sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
                    } else {
                        sendMessage.setText("Ни кого нѣтъ.");
                    }
                } else {
                    User favorite = users.getProfileForUserFavorites(userId);
                    if (favorite != null) {
                        botStateCache.saveBotState(userId, BotState.FAVORITES);
                        sendMessage.setText(favorite.toString());
                        sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
                    } else {
                        sendMessage.setText("У васъ нѣтъ любимцевъ.");
                    }
                }

                return sendMessage;

            case "SEARCH":
                if ("buttonLeft".equals(data)) {
                    sendMessage.setText(users.getProfileForUser(userId).toString());
                    sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
                } else if ("buttonRight".equals(data)) {
                    String answerFavorite = restTemplate.postForObject(
                            "http://localhost:8080/users/" + userId + "/like",
                            new HttpEntity<>(users.getFoundUserId()),
                            String.class
                    );

                    sendMessage.setText(answerFavorite + "\n\n" +
                            users.getProfileForUser(userId));

                    sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
                } else {
                    botStateCache.saveBotState(userId, BotState.MENU);
                    sendMessage.setText("Возврат в меню");
                    sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
                }

                return sendMessage;

            case "FAVORITES":
                if ("buttonLeft".equals(data)) {
                    sendMessage.setText(users.userFavoritesLeft(userId).toString());
                    sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
                } else if ("buttonRight".equals(data)) {
                    sendMessage.setText(users.userFavoritesRight(userId).toString());
                    sendMessage.setReplyMarkup(buttonService.getInlineMessageSearch());
                } else {
                    botStateCache.saveBotState(userId, BotState.MENU);
                    sendMessage.setText("Возврат в меню");
                    sendMessage.setReplyMarkup(buttonService.getInlineMessageMenu());
                }

                return sendMessage;

            default:
                return null;
        }
    }
}
