package ru.liga.telegrambot.caches;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.liga.telegrambot.telegram.BotState;

import java.util.HashMap;
import java.util.Map;

/**
 * Кеш для хранения состояний бота для каждого пользователя.
 */
@Getter
@Service
public class BotStateCache {
    /**
     * Карта состояний.
     */
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    /**
     * Сохранить в кеше состояние бота для определённого пользователя.
     *
     * @param userId   id пользователя
     * @param botState состояние бота
     */
    public void saveBotState(long userId, BotState botState) {
        botStateMap.put(userId, botState);
    }
}
