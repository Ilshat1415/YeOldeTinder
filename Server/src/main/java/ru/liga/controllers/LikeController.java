package ru.liga.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;

@RestController
@RequestMapping("/users/{userId}")
public class LikeController {
    private final UsersRepository usersRepository;

    public LikeController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @PostMapping("/like")
    public String likeUserById(@PathVariable long userId, HttpEntity<Long> favoriteId) {
        User user = usersRepository.getById(userId);
        user.getFavorites().add(usersRepository.getById(favoriteId.getBody()));
        usersRepository.save(user);
        return "";
    }
}
