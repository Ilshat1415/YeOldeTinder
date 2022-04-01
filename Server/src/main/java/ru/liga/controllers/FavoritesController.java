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
public class FavoritesController {
    private final UsersRepository usersRepository;

    public FavoritesController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/favorites")
    public LinkedList<User> getFavorites(@PathVariable Long userId) {
        User userById = usersRepository.getById(userId);
        List<User> firstList = usersRepository.findAll()
                .stream()
                .filter(user -> user.getFavorites().contains(userById))
                .collect(Collectors.toList());
        List<User> secondList = userById.getFavorites();

        LinkedList<User> reciprocity = new LinkedList<>();
        LinkedList<User> loveUser = new LinkedList<>();
        LinkedList<User> favoriteUser = new LinkedList<>();

        for (User user : firstList) {
            if (secondList.contains(user)) {
                secondList.remove(user);
                user.setName(user.getName() + ", Взаимность.");
                reciprocity.add(user);
            } else {
                user.setName(user.getName() + ", Вы любимы.");
                loveUser.add(user);
            }
        }

        secondList.forEach(user -> {
            user.setName(user.getName() + ", Любим.");
            favoriteUser.add(user);
        });

        favoriteUser.addAll(loveUser);
        favoriteUser.addAll(reciprocity);

        return favoriteUser;
    }
}
