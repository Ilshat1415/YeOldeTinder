package ru.liga.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;
import ru.liga.service.ImageEncodingService;
import ru.liga.service.UsersService;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class UsersController {
    private final ImageEncodingService imageEncodingService;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    @GetMapping
    public boolean foundUserById(@PathVariable long userId) {
        return usersRepository.existsById(userId);
    }

    @GetMapping("/get")
    public User getUserById(@PathVariable long userId) {
        return usersRepository.getById(userId);
    }

    @GetMapping("/getImage")
    public User getUserImageById(@PathVariable long userId) {
        User user = usersRepository.getById(userId);
        imageEncodingService.encodeTheDescription(user);
        return user;
    }

    @GetMapping("/search")
    public LinkedList<User> getUsers(@PathVariable long userId) {
        List<User> users = usersService.getSearchedUsersByUserId(userId);
        return users.stream().peek(imageEncodingService::encodeTheDescription).collect(Collectors.toCollection(LinkedList::new));
    }

    @GetMapping("/favorites")
    public LinkedList<User> getFavorites(@PathVariable Long userId) {
        List<User> favorites = usersService.getFavoritesByUserId(userId);
        return favorites.stream().peek(imageEncodingService::encodeTheDescription).collect(Collectors.toCollection(LinkedList::new));
    }

    @PostMapping("/like")
    public String likeUserById(@PathVariable long userId, HttpEntity<Long> favoriteId) {
        User user = usersRepository.getById(userId);
        User favorite = usersRepository.getById(favoriteId.getBody());

        user.getFavorites().add(favorite);
        usersRepository.save(user);

        return favorite.getFavorites().contains(user) ? "Вы любимы" : null;
    }
}
