package ru.liga.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.liga.entities.User;
import ru.liga.repositories.UsersRepository;

@RestController
public class CreateProfileController {

    private final UsersRepository usersRepository;

    public CreateProfileController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @PostMapping("/createProfile")
    public String signUpUser(HttpEntity<User> request) {
        usersRepository.save(request.getBody());
        return "Регистрация прошла успешно";
    }
}
