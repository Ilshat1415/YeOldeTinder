package ru.liga.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.entities.User;
import ru.liga.service.ImageEncodingService;
import ru.liga.service.UsersService;

/**
 * Контроллер отвечающий за создания профиля по запросу.
 */
@RestController
@RequiredArgsConstructor
public class CreateProfileController {
    /**
     * Сервис создания закодированных образов анкет
     */
    private final ImageEncodingService imageEncodingService;
    /**
     * Сервис работы с пользователями.
     */
    private final UsersService usersService;

    /**
     * Реакция на POST запрос создания профиля.
     *
     * @param request запрос
     * @return профиль ользователя
     */
    @PostMapping("/createProfile")
    public User signUpUser(HttpEntity<User> request) {
        User user = request.getBody();
        usersService.createProfileUser(user);
        imageEncodingService.encodeTheDescription(user);

        return user;
    }
}
