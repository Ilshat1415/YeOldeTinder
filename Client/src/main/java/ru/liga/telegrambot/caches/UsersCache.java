package ru.liga.telegrambot.caches;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.liga.telegrambot.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Кеш для временного хранения пользователей.
 */
@Getter
@Setter
@Service
public class UsersCache {
    /**
     * Карта пользователей.
     */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Сохранение в кеше уникального пользователя.
     *
     * @param userId id пользователя
     * @param user   пользователь
     */
    public void saveUserCache(long userId, User user) {
        users.put(userId, user);
    }

    /**
     * Очистка уникального пользователя по id.
     *
     * @param userId id пользователя
     */
    public void dump(long userId) {
        users.remove(userId);
    }
}
