package ru.liga.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;
import ru.liga.utils.PreReformTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис работы с пользователями.
 */
@Service
@RequiredArgsConstructor
public class UsersService {
    /**
     * Репозиторий пользователей.
     */
    private final UsersRepository usersRepository;
    /**
     * Переводчик на старославянский язык.
     */
    private final PreReformTranslator preReformTranslator;

    /**
     * Создание профиля.
     *
     * @param user пользователь
     */
    public void createProfileUser(User user) {
        user.setName(preReformTranslator.translateName(user.getName()));
        if (user.getName().length() > 60) {
            user.setName(user.getName().substring(0, 59));
        }

        user.setDescription(preReformTranslator.translateName(user.getDescription()));
        if (user.getDescription().length() > 1024) {
            user.setDescription(user.getDescription().substring(0, 1023));
        }

        if (usersRepository.existsById(user.getId())) {
            user.setFavorites(usersRepository.getById(user.getId()).getFavorites());
        }

        usersRepository.save(user);
    }

    /**
     * Отбор пользователей по искомому запросу.
     *
     * @param userId id пользователя
     * @return список искомых пользователей
     */
    public List<User> getSearchedUsersByUserId(Long userId) {
        User user = usersRepository.getById(userId);

        List<User> users = usersRepository.findAll()
                .stream()
                .filter(searchedUser -> !searchedUser.getId().equals(userId))
                .filter(searchedUser -> !user.getFavorites().contains(searchedUser))
                .filter(searchedUser -> predicateByGender(user, searchedUser))
                .collect(Collectors.toList());

        if (!"Всех".equals(user.getGenderSearch())) {
            users = users.stream()
                    .filter(searchedUser -> searchedUser.getGender().equals(user.getGenderSearch()))
                    .collect(Collectors.toList());
        }

        return users;
    }

    /**
     * Отбор любимцев из репозитория.
     *
     * @param userId id пользователя
     * @return список любимцев
     */
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
            user.setName(user.getName() + ", Любим" + (user.getGender().equals("Сударь") ? "ъ." : "a."));
            favorites.add(user);
        });

        return favorites;
    }

    /**
     * Предикат для отбора по половому признаку
     *
     * @param user         пользователь
     * @param searchedUser искомый пользователь
     * @return true если пол пользователя совпадает с предпочтениями искомого пользователя, иначе false
     */
    private boolean predicateByGender(User user, User searchedUser) {
        if (!"Всех".equals(searchedUser.getGenderSearch())) {
            return user.getGender().equals(searchedUser.getGenderSearch());
        } else {
            return true;
        }
    }
}
