package ru.liga.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;
import ru.liga.service.ImageEncodingService;
import ru.liga.service.UsersService;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Контроллер отвечающий за обработкку запросов users.
 */
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class UsersController {
    /**
     * Сервис создания закодированных образов анкет
     */
    private final ImageEncodingService imageEncodingService;
    /**
     * Репозиторий пользователей.
     */
    private final UsersRepository usersRepository;
    /**
     * Сервис работы с пользователями.
     */
    private final UsersService usersService;

    /**
     * Поиск пользователя в репозитории.
     *
     * @param userId id пользователя
     * @return true если пользователь найден, иначе false
     */
    @GetMapping
    public boolean foundUserById(@PathVariable long userId) {
        return usersRepository.existsById(userId);
    }

    /**
     * Достаёт пользователя из репозитория.
     *
     * @param userId id пользователя
     * @return пользователь
     */
    @GetMapping("/get")
    public User getUserById(@PathVariable long userId) {
        return usersRepository.getById(userId);
    }

    /**
     * Возвращает подготовленный профиль пользователя.
     *
     * @param userId id пользователя
     * @return профиль пользователя
     */
    @GetMapping("/getImage")
    public User getUserImageById(@PathVariable long userId) {
        User user = usersRepository.getById(userId);

        imageEncodingService.encodeTheDescription(user);

        return user;
    }

    /**
     * В ответ на запрос возвращает профили искомых пользователей.
     *
     * @param userId id пользователя
     * @return список искомых пользователей
     */
    @GetMapping("/search")
    public LinkedList<User> getUsers(@PathVariable long userId) {
        return usersService.getSearchedUsersByUserId(userId)
                .stream()
                .peek(imageEncodingService::encodeTheDescription)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * В ответ на запрос возвращает профили любимцев.
     *
     * @param userId id пользователя
     * @return список любимцев
     */
    @GetMapping("/favorites")
    public LinkedList<User> getFavorites(@PathVariable long userId) {
        return usersService.getFavoritesByUserId(userId)
                .stream()
                .peek(imageEncodingService::encodeTheDescription)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Реакция на POST запрос добавления любимца.
     *
     * @param userId
     * @param favoriteId
     * @return результат взаимности.
     */
    @PostMapping("/like")
    public String likeUserById(@PathVariable long userId, HttpEntity<Long> favoriteId) {
        User user = usersRepository.getById(userId);
        User favorite = usersRepository.getById(favoriteId.getBody());

        user.getFavorites().add(favorite);
        usersRepository.save(user);

        return favorite.getFavorites().contains(user) ? "Вы любимы" : null;
    }
}
