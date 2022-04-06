package ru.liga.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;

    public List<User> getSearchedUsersByUserId(Long userId) {
        User userById = usersRepository.getById(userId);

        List<User> users = usersRepository.findAll()
                .stream()
                .filter(user -> !user.getId().equals(userId))
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

        return users;
    }

    public List<User> getFavoritesByUserId(Long userId) {
        User userById = usersRepository.getById(userId);
        List<User> favoriteUser = userById.getFavorites();
        List<User> loveUser = usersRepository.findAll()
                .stream()
                .filter(user -> user.getFavorites().contains(userById))
                .collect(Collectors.toList());

        List<User> favorites = new ArrayList<>();
        for (User user : loveUser) {
            if (favoriteUser.contains(user)) {
                favoriteUser.remove(user);
                user.setName(user.getName() + ", Взаимность.");
                favorites.add(user);
            } else {
                user.setName(user.getName() + ", Вы любимы.");
                favorites.add(user);
            }
        }
        favoriteUser.forEach(user -> {
            user.setName(user.getName() + ", Любим" + (user.getGender().equals("Сударъ") ? "ъ." : "a."));
            favorites.add(user);
        });

        return favorites;
    }
}
