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
    private final Map<Long, LinkedList<User>> favoritesMap = new HashMap<>();
    private final ServerDataService serverDataService;

    public User getProfileForUserFavorites(long userId) {
        favoritesMap.put(userId, serverDataService.getFavoritesById(userId));

        return favoritesMap.get(userId).peek();
    }

    public User userFavoritesLeft(long userId) {
        Collections.rotate(favoritesMap.get(userId), 1);

        return favoritesMap.get(userId).peek();
    }

    public User userFavoritesRight(long userId) {
        Collections.rotate(favoritesMap.get(userId), -1);

        return favoritesMap.get(userId).peek();
    }
}
