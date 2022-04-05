package ru.liga.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.telegrambot.entities.User;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class ServerDataService {
    private final RestTemplate restTemplate;

    @Value("${server.api-url}")
    private String serverPath;

    public boolean existsUserById(long userId) {
        return Boolean.TRUE.equals(restTemplate.getForObject(serverPath + "/users/" + userId, boolean.class));
    }

    public User getUserById(long userId) {
        return restTemplate.getForObject(serverPath + "/users/" + userId + "/get", User.class);
    }

    public User getUserImageById(long userId) {
        return restTemplate.getForObject(serverPath + "/users/" + userId + "/getImage", User.class);
    }

    public User createProfile(User user) {
        return restTemplate.postForObject(
                "http://localhost:8080/createProfile",
                user,
                User.class
        );
    }

    public String likeUserById(long userId, long foundUserId) {
        return restTemplate.postForObject(
                serverPath + "/users/" + userId + "/like",
                new HttpEntity<>(foundUserId),
                String.class
        );
    }

    public LinkedList<User> getSearchedUsersById(long userId) {
        return restTemplate.exchange(
                serverPath + "/users/" + userId + "/search",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<LinkedList<User>>() {
                }
        ).getBody();
    }

    public LinkedList<User> getFavoritesById(long userId) {
        return restTemplate.exchange(
                serverPath + "/users/" + userId + "/favorites",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<LinkedList<User>>() {
                }
        ).getBody();
    }
}
