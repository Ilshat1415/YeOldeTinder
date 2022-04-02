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
public class SearchedUsersCache {
    private final Map<Long, LinkedList<User>> searchedUsers = new HashMap<>();
    private final ServerDataService serverDataService;

    public User getSearchedUsersById(long userId) {
        if (searchedUsers.get(userId) == null) {
            return null;
        }
        User user = searchedUsers.get(userId).getFirst();

        Collections.rotate(searchedUsers.get(userId), -1);

        return user;
    }

    public Long getFoundUserId(long userId) {
        return searchedUsers.get(userId).removeLast().getId();
    }

    public void refreshCache(long userId) {
        searchedUsers.put(userId, serverDataService.getSearchedUsersById(userId));
    }
}
