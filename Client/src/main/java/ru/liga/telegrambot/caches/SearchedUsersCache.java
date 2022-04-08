package ru.liga.telegrambot.caches;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.ServerDataService;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Кеш для временного хранения списка искомых пользователей.
 */
@Service
@RequiredArgsConstructor
public class SearchedUsersCache {
    /**
     * Карта искомых пользователей.
     */
    private final Map<Long, LinkedList<User>> searchedUsers = new HashMap<>();
    /**
     * Сервис серверных данных.
     */
    private final ServerDataService serverDataService;

    /**
     * Получение следующего искомого пользователя из кеша.
     *
     * @param userId id пользователя
     * @return искомый пользователь
     */
    public User getSearchedUsersById(long userId) {
        if (searchedUsers.get(userId).isEmpty()) {
            refresh(userId);

            if (searchedUsers.get(userId).isEmpty()) {
                return null;
            }
        }

        User user = searchedUsers.get(userId).getFirst();

        Collections.rotate(searchedUsers.get(userId), -1);

        return user;
    }

    /**
     * Получение id последнего искомого пользователя.
     *
     * @param userId id пользователя
     * @return id искомого пользователя
     */
    public Long getFoundUserById(long userId) {
        return searchedUsers.get(userId).removeLast().getId();
    }

    /**
     * Обновить кеш для определённого пользователя.
     *
     * @param userId id пользователя
     */
    public void refresh(long userId) {
        searchedUsers.put(userId, serverDataService.getSearchedUsersById(userId));
    }

    /**
     * Очистить кеш для определённого пользователя.
     *
     * @param userId id пользователя
     */
    public void dump(long userId) {
        searchedUsers.remove(userId);
    }
}
