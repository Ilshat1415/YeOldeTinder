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

/**
 * Сервис для получения данных с сервера.
 */
@Service
@RequiredArgsConstructor
public class ServerDataService {
    /**
     * Объект RestTemplate.
     */
    private final RestTemplate restTemplate;
    /**
     * Путь к серверу.
     */
    @Value("${server.api-url}")
    private String serverPath;

    /**
     * Запрос серверу на проверку наличия пользователя.
     *
     * @param userId id пользователя
     * @return true если такой пользователь существует, иначе false
     */
    public boolean existsUserById(long userId) {
        return Boolean.TRUE.equals(
                restTemplate.getForObject(
                        serverPath + "/users/" + userId,
                        boolean.class
                )
        );
    }

    /**
     * Запрос серверу на получения пользователя из БД.
     *
     * @param userId id пользователя
     * @return пользователь
     */
    public User getUserById(long userId) {
        return restTemplate.getForObject(
                serverPath + "/users/" + userId + "/get",
                User.class
        );
    }

    /**
     * Запрос серверу на получения анкеты пользователя.
     *
     * @param userId id пользователя
     * @return пользователь с образом
     */
    public User getUserImageById(long userId) {
        return restTemplate.getForObject(
                serverPath + "/users/" + userId + "/getImage",
                User.class
        );
    }

    /**
     * Запрос серверу на создание нового пользователя.
     *
     * @param user id пользователя
     * @return пользователь
     */
    public User createProfile(User user) {
        return restTemplate.postForObject(
                serverPath + "/createProfile",
                user,
                User.class
        );
    }

    /**
     * Запрос серверу на добавление пользователю любимца.
     *
     * @param userId     id пользователя
     * @param favoriteId id любимца
     * @return ответ на взаимность
     */
    public String likeUserById(long userId, long favoriteId) {
        return restTemplate.postForObject(
                serverPath + "/users/" + userId + "/like",
                new HttpEntity<>(favoriteId),
                String.class
        );
    }

    /**
     * Запрос серверу на получение списка искомых пользователей.
     *
     * @param userId id пользователя
     * @return список искомых пользователей
     */
    public LinkedList<User> getSearchedUsersById(long userId) {
        return restTemplate.exchange(
                serverPath + "/users/" + userId + "/search",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<LinkedList<User>>() {
                }
        ).getBody();
    }

    /**
     * Запрос серверу на получение списка любимцев пользователя.
     *
     * @param userId id пользователя
     * @return список любимцев
     */
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
