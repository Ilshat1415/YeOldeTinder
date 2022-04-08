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
 * Кеш для временного хранения любимцев пользователей.
 */
@Service
@RequiredArgsConstructor
public class FavoritesCache {
    /**
     * Карта любимцев.
     */
    private final Map<Long, LinkedList<User>> favorites = new HashMap<>();
    /**
     * Сервис серверных данных.
     */
    private final ServerDataService serverDataService;

    /**
     * Получение любимца.
     *
     * @param userId id пользователя
     * @return любимец
     */
    public User getProfileForUserFavorites(long userId) {
        favorites.put(userId, serverDataService.getFavoritesById(userId));

        return favorites.get(userId).peek();
    }

    /**
     * Получение следующего любимца из кеша.
     *
     * @param userId id пользователя
     * @return любимец
     */
    public User userFavoritesLeft(long userId) {
        Collections.rotate(favorites.get(userId), 1);

        return favorites.get(userId).peek();
    }

    /**
     * Получение предыдущего любимца из кеша.
     *
     * @param userId id пользователя
     * @return любимец
     */
    public User userFavoritesRight(long userId) {
        Collections.rotate(favorites.get(userId), -1);

        return favorites.get(userId).peek();
    }

    /**
     * Очистка кеша для определённого пользователя.
     *
     * @param userId id пользователя
     */
    public void dump(long userId) {
        favorites.remove(userId);
    }
}
