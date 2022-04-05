package ru.liga.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;
import ru.liga.service.ImageEncodingService;
import ru.liga.utils.PreReformTranslator;

@RestController
@RequiredArgsConstructor
public class CreateProfileController {
    private final ImageEncodingService imageEncodingService;
    private final PreReformTranslator preReformTranslator;
    private final UsersRepository usersRepository;

    @PostMapping("/createProfile")
    public User signUpUser(HttpEntity<User> request) {
        User user = request.getBody();
        user.setName(preReformTranslator.translateName(user.getName()));

        if (user.getDescription().length() > 255) {
            user.setDescription(user.getDescription().substring(0, 254));
        }

        user.setDescription(preReformTranslator.translateName(user.getDescription()));

        if (user.getDescription().length() > 512) {
            user.setDescription(user.getDescription().substring(0, 511));
        }

        usersRepository.save(user);
        imageEncodingService.encodeTheDescription(user);

        return user;
    }
}
