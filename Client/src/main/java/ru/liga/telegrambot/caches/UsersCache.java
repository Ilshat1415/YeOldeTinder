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
public class UsersCache {
    private final Map<Long, User> users = new HashMap<>();

    public void saveUserCache(long userId, User user) {
        users.put(userId, user);
    }

    public void dump(long userId) {
        users.remove(userId);
    }
}
