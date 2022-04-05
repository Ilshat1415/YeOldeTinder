package ru.liga.telegrambot.caches;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.telegrambot.entities.User;
import ru.liga.telegrambot.service.ServerDataService;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoritesCache {
    private final Map<Long, LinkedList<User>> favorites = new HashMap<>();
    private final ServerDataService serverDataService;

    public User getProfileForUserFavorites(long userId) {
        favorites.put(userId, serverDataService.getFavoritesById(userId));

        return favorites.get(userId).peek();
    }

    public User userFavoritesLeft(long userId) {
        Collections.rotate(favorites.get(userId), 1);

        return favorites.get(userId).peek();
    }

    public User userFavoritesRight(long userId) {
        Collections.rotate(favorites.get(userId), -1);

        return favorites.get(userId).peek();
    }

    public void dump(long userId) {
        favorites.remove(userId);
    }
}
