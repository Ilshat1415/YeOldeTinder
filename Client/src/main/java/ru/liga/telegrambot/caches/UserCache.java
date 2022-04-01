package ru.liga.telegrambot.caches;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.liga.telegrambot.entities.User;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Service
public class UserCache {
    private final Map<Long, User> userMap = new HashMap<>();

    public void saveUserCache(long userId, User user) {
        userMap.put(userId, user);
    }
}
