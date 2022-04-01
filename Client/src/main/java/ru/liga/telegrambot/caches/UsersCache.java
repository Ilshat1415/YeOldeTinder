package ru.liga.telegrambot.caches;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.telegrambot.entities.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
@Service
public class UsersCache {
    private final Map<Long, LinkedList<User>> usersMap = new HashMap<>();
    private final Map<Long, LinkedList<User>> favoritesMap = new HashMap<>();
    private Long foundUserId;

    public User getProfileForUser(long userId) {
        LinkedList<User> users = usersMap.get(userId);
        if (users == null || users.isEmpty()) {
            usersMap.put(userId, saveUsersCache(userId, "/search"));
        }

        User user = usersMap.get(userId).poll();
        if (user == null) {
            return null;
        }

        foundUserId = user.getId();
        return user;
    }

    public User getProfileForUserFavorites(long userId) {
        favoritesMap.put(userId, saveUsersCache(userId, "/favorites"));

        if (favoritesMap.get(userId).isEmpty()) {
            return null;
        }
        return favoritesMap.get(userId).getFirst();
    }

    public User userFavoritesLeft(long userId) {
        Collections.rotate(favoritesMap.get(userId), 1);

        return favoritesMap.get(userId).getFirst();
    }

    public User userFavoritesRight(long userId) {
        Collections.rotate(favoritesMap.get(userId), -1);

        return favoritesMap.get(userId).getFirst();
    }

    private LinkedList<User> saveUsersCache(long userId, String request) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<LinkedList<User>> responseEntity = restTemplate.exchange(
                "http://localhost:8080/users/" + userId + request,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<LinkedList<User>>() {
                }
        );
        return responseEntity.getBody();
    }
}
