package ru.liga.telegrambot.caches;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.liga.telegrambot.telegram.BotState;

import java.util.HashMap;
import java.util.Map;

@Getter
@Service
public class BotStateCache {
    private final Map<Long, BotState> botStateMap = new HashMap<>();

    public void saveBotState(long userId, BotState botState) {
        botStateMap.put(userId, botState);
    }
}
