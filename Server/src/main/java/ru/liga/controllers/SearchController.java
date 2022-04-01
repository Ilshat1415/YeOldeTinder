package ru.liga.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}")
public class SearchController {
    private final UsersRepository usersRepository;

    public SearchController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/search")
    public LinkedList<User> getUsers(@PathVariable long userId) {
        User userById = usersRepository.getById(userId);

        List<User> users = usersRepository.findAll()
                .stream()
                .filter(user -> user.getId() != userId)
                .filter(user -> !userById.getFavorites().contains(user))
                .filter(user -> {
                    if (!"Всех".equals(user.getGenderSearch())) {
                        return userById.getGender().equals(user.getGenderSearch());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());

        if (!"Всех".equals(userById.getGenderSearch())) {
            users = users.stream()
                    .filter(user -> user.getGender().equals(userById.getGenderSearch()))
                    .collect(Collectors.toList());
        }

        return new LinkedList<>(users);
    }
}
